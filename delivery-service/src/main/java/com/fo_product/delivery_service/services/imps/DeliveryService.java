package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.clients.OrderClient;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import com.fo_product.delivery_service.exceptions.DeliveryException;
import com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode;
import com.fo_product.delivery_service.helpers.GetClientDTO;
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
import org.springframework.stereotype.Service;

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

    @Override
    public void acceptOrder(Long userId, Long orderId) {
        Shipper shipper = shipperRepository.findByUserId(userId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.SHIPPER_NOT_FOUND));

        // Check race condition (đã có ai nhận chưa)
        if (deliveryRepository.existsByOrderId(orderId)) {
            throw new DeliveryException(DeliveryErrorCode.ORDER_ALREADY_TAKEN);
        }

        // Tạo Delivery record
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .shipper(shipper)
                .status(DeliveryStatus.ACCEPTED) // Trạng thái của riêng bảng Delivery
                .build();
        deliveryRepository.save(delivery);

        // Xóa khỏi danh sách tìm kiếm để không báo cho shipper khác nữa
        orderMatchingService.removeFromPendingQueue(orderId);

        log.info("Shipper {} đã nhận đơn {}", userId, orderId);
    }

    @Override
    public void updatePickedUp(Long userId, Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        // Update bảng Delivery local
        delivery.setStatus(DeliveryStatus.DELIVERING);
        deliveryRepository.save(delivery);

        // --- GỌI SANG ORDER SERVICE ---
        orderClient.markAsDelivering(orderId);
    }

    @Override
    public void completeOrder(Long userId, Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        // Update bảng Delivery local
        delivery.setStatus(DeliveryStatus.COMPLETED);
        deliveryRepository.save(delivery);

        // --- GỌI SANG ORDER SERVICE ---
        // Để bên đó chốt đơn, tính doanh thu v.v.
        orderClient.markAsCompleted(orderId);

        // 4. Lấy thông tin đơn hàng để biết phí ship (Doanh thu của Shipper)
        OrderDTO orderRes = getClientDTO.getOrderDTO(orderId);
        BigDecimal shippingFee = orderRes.shippingFee();

        if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Đơn hàng {} có phí ship = 0, không cộng ví.", orderId);
            return;
        }

        // 5. Cộng tiền vào ví Shipper
        Shipper shipper = delivery.getShipper();

        // Tìm ví, nếu chưa có thì tạo mới (Safe logic)
        ShipperWallet wallet = walletRepository.findByShipper_Id(shipper.getId())
                .orElseGet(() -> {
                    ShipperWallet newWallet = ShipperWallet.builder()
                            .shipper(shipper)
                            .balance(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });

        // Cộng tiền
        BigDecimal oldBalance = wallet.getBalance();
        BigDecimal newBalance = oldBalance.add(shippingFee);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // 6. Ghi lịch sử giao dịch (Transaction History)
        ShipperTransaction transaction = ShipperTransaction.builder()
                .wallet(wallet)
                .amount(shippingFee)
                .type(TransactionType.INCOME) // Enum: INCOME (Cộng), WITHDRAW (Rút)
                .description("Thu nhập từ đơn hàng #" + orderId)
                .build();

        transactionRepository.save(transaction);

        log.info("Shipper {} hoàn thành đơn {}. +{} VND. Số dư mới: {}",
                shipper.getId(), orderId, shippingFee, newBalance);
    }
}
