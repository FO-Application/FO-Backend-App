package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.clients.OrderClient;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import com.fo_product.delivery_service.dtos.feigns.UserDTO; // Import UserDTO
import com.fo_product.delivery_service.exceptions.DeliveryException;
import com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode;
import com.fo_product.delivery_service.helpers.GetClientDTO;
import com.fo_product.delivery_service.kafka.KafkaProducerService;
import com.fo_product.delivery_service.kafka.events.ShipperAssignedEvent;
import com.fo_product.delivery_service.models.entities.Delivery;
import com.fo_product.delivery_service.models.entities.Shipper;
import com.fo_product.delivery_service.models.entities.ShipperTransaction;
import com.fo_product.delivery_service.models.entities.ShipperWallet;
import com.fo_product.delivery_service.models.enums.DeliveryStatus;
import com.fo_product.delivery_service.models.enums.TransactionType;
import com.fo_product.delivery_service.models.repositories.DeliveryRepository;
import com.fo_product.delivery_service.models.repositories.ShipperRepository;
import com.fo_product.delivery_service.models.repositories.ShipperTransactionRepository;
import com.fo_product.delivery_service.models.repositories.ShipperWalletRepository;
import com.fo_product.delivery_service.services.interfaces.IDeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryService implements IDeliveryService {
    DeliveryRepository deliveryRepository;
    ShipperRepository shipperRepository;
    OrderMatchingService orderMatchingService;
    OrderClient orderClient;
    ShipperWalletRepository walletRepository;
    ShipperTransactionRepository transactionRepository;
    GetClientDTO getClientDTO;
    KafkaProducerService kafkaProducerService;

    @Override
    @Transactional // Quan trọng để đảm bảo tính toàn vẹn
    public void acceptOrder(Long userId, Long orderId) {
        Shipper shipper = shipperRepository.findByUserId(userId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.SHIPPER_NOT_FOUND));

        // 1. Lấy thông tin User để bắn Kafka (Tên, SĐT)
        UserDTO user = getClientDTO.getUserDTO(userId);

        // 2. Tạo đối tượng Delivery
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .shipper(shipper)
                .status(DeliveryStatus.ACCEPTED)
                .build();

        try {
            // [LOGIC CHUẨN]: Chỉ save 1 lần duy nhất trong khối try-catch
            // Nhờ Unique Constraint ở DB, nếu orderId trùng -> Ném DataIntegrityViolationException
            deliveryRepository.save(delivery);

            // 3. Xóa đơn khỏi hàng đợi tìm kiếm (Redis)
            orderMatchingService.removeFromPendingQueue(orderId);

            // 4. Bắn Kafka báo Order Service
            ShipperAssignedEvent event = ShipperAssignedEvent.builder()
                    .orderId(orderId)
                    .shipperId(shipper.getId())
                    .shipperName(user.firstName() + " " + user.lastName())
                    .shipperPhone(user.phone())
                    .licensePlate(shipper.getVehicleNumber())
                    .build();

            kafkaProducerService.sendShipperAssignedEvent(event);

            log.info("Shipper {} đã nhận đơn {} thành công", userId, orderId);

        } catch (DataIntegrityViolationException e) {
            // 5. Bắt lỗi tranh chấp đơn
            log.warn("Shipper {} chậm tay, đơn {} đã có người nhận.", userId, orderId);
            throw new DeliveryException(DeliveryErrorCode.ORDER_ALREADY_TAKEN);
        }
    }

    @Override
    public void updatePickedUp(Long userId, Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        delivery.setStatus(DeliveryStatus.DELIVERING);
        deliveryRepository.save(delivery);

        // Gọi sang Order Service
        orderClient.markAsDelivering(orderId);
    }

    @Override
    @Transactional // Transaction cho việc cộng tiền ví
    public void completeOrder(Long userId, Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        delivery.setStatus(DeliveryStatus.COMPLETED);
        deliveryRepository.save(delivery);

        // Gọi Order Service chốt đơn
        orderClient.markAsCompleted(orderId);

        // Logic cộng tiền ví
        OrderDTO orderRes = getClientDTO.getOrderDTO(orderId);
        BigDecimal shippingFee = orderRes.shippingFee();

        if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Đơn hàng {} có phí ship = 0", orderId);
            return;
        }

        Shipper shipper = delivery.getShipper();

        // Tìm hoặc tạo ví mới
        ShipperWallet wallet = walletRepository.findByShipper_Id(shipper.getId())
                .orElseGet(() -> {
                    ShipperWallet newWallet = ShipperWallet.builder()
                            .shipper(shipper)
                            .balance(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });

        // Update số dư
        BigDecimal newBalance = wallet.getBalance().add(shippingFee);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Ghi log giao dịch
        transactionRepository.save(ShipperTransaction.builder()
                .wallet(wallet)
                .amount(shippingFee)
                .type(TransactionType.INCOME)
                .description("Thu nhập từ đơn hàng #" + orderId)
                .build());

        log.info("Shipper {} +{} VND. Số dư: {}", shipper.getId(), shippingFee, newBalance);
    }
}