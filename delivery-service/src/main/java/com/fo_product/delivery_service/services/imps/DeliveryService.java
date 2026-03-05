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
import java.util.List;

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
        // [FIX] Handle duplicate deliveries (resilience)
        List<Delivery> deliveries = deliveryRepository.findAllByOrderId(orderId);
        
        if (deliveries.isEmpty()) {
            throw new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND);
        }

        Delivery delivery = deliveries.get(0); // Pick the first one
        if (deliveries.size() > 1) {
            log.warn("Found {} delivery records for order {}. Using the first one (ID: {}). Possible duplication bug.", 
                    deliveries.size(), orderId, delivery.getId());
            // Optional: Logic to delete duplicates if needed, but for now just proceed
        }

        delivery.setStatus(DeliveryStatus.DELIVERING);
        deliveryRepository.save(delivery);

        // Gọi sang Order Service
        orderClient.markAsDelivering(orderId);
    }

    @Override
    @Transactional // Transaction cho việc cộng tiền ví
    public void completeOrder(Long userId, Long orderId) {
        // [FIX] Handle duplicate deliveries (resilience)
        List<Delivery> deliveries = deliveryRepository.findAllByOrderId(orderId);

        if (deliveries.isEmpty()) {
            throw new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND);
        }

        Delivery delivery = deliveries.get(0); // Pick the first one
        if (deliveries.size() > 1) {
            log.warn("Found {} delivery records for order {}. Using the first one (ID: {}).", 
                    deliveries.size(), orderId, delivery.getId());
        }

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

        // --- XỬ LÝ COD ---
        // Nếu là COD, Shipper thu tiền mặt từ khách (Food + Ship).
        // Shipper giữ tiền Ship (đã có trong tay).
        // Shipper giữ tiền Food (của quán).
        // => Hệ thống phải TRỪ tiền Food từ ví Shipper để trả cho Quán.
        if ("COD".equals(orderRes.paymentMethod())) {
            BigDecimal foodMoney = orderRes.grandTotal().subtract(shippingFee);
            if (foodMoney.compareTo(BigDecimal.ZERO) > 0) {
                // Tr trừ ví
                wallet.setBalance(wallet.getBalance().subtract(foodMoney));
                walletRepository.save(wallet);

                // Ghi log Trừ tiền
                transactionRepository.save(ShipperTransaction.builder()
                        .wallet(wallet)
                        .amount(foodMoney.negate()) // Ghi âm
                        .type(TransactionType.WITHDRAW) // Hoặc loại transaction khác nếu có
                        .description("Trừ tiền thu hộ đơn COD #" + orderId)
                        .build());
                
                log.info("Shipper {} thu hộ {} đ. Đã trừ ví.", shipper.getId(), foodMoney);
            }
        }

        // --- CỘNG TIỀN SHIP (INCOME) ---
        // Chỉ cộng shippingFee vào ví cho thanh toán ONLINE (hệ thống thu tiền, cần trả ship cho shipper).
        // Với COD: shipper đã cầm tiền ship trong tay rồi → KHÔNG cộng thêm (tránh tính đúp).
        if (!"COD".equals(orderRes.paymentMethod())) {
            BigDecimal newBalance = wallet.getBalance().add(shippingFee);
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);

            transactionRepository.save(ShipperTransaction.builder()
                    .wallet(wallet)
                    .amount(shippingFee)
                    .type(TransactionType.INCOME)
                    .description("Thu nhập phí ship đơn hàng #" + orderId)
                    .build());

            log.info("Shipper {} +{} VND (Online). Số dư: {}", shipper.getId(), shippingFee, newBalance);
        } else {
            log.info("Shipper {} - Đơn COD, tiền ship đã cầm tay. Không cộng ví.", shipper.getId());
        }
    }

    @Override
    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DeliveryException(DeliveryErrorCode.INVALID_REQUEST); // Tạm dùng error code này
        }

        Shipper shipper = shipperRepository.findByUserId(userId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.SHIPPER_NOT_FOUND));

        ShipperWallet wallet = walletRepository.findByShipper_Id(shipper.getId())
                .orElseGet(() -> {
                     return walletRepository.save(ShipperWallet.builder()
                            .shipper(shipper)
                            .balance(BigDecimal.ZERO)
                            .build());
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionRepository.save(ShipperTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.INCOME) // Dùng INCOME vì DB chưa có ENUM DEPOSIT (lỗi truncated)
                .description("Nạp tiền vào ví") 
                .build());
        
        log.info("Shipper {} nạp {} VND. Số dư mới: {}", userId, amount, wallet.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse getWalletStats(Long userId) {
        Shipper shipper = shipperRepository.findByUserId(userId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.SHIPPER_NOT_FOUND));

        ShipperWallet wallet = walletRepository.findByShipper_Id(shipper.getId())
                .orElseGet(() -> ShipperWallet.builder()
                        .balance(BigDecimal.ZERO)
                        .build());
        
        // Tính toán thống kê từ Transaction (nếu có logic phức tạp hơn thì query DB)
        // Hiện tại chỉ tính Balance. 
        // Các mục Income Today/Week/Month nên query từ ShipperTransactionRepository.
        // Tạm thời trả về 0 cho các mục Stats khác nếu chưa cần thiết, hoặc query nếu muốn xịn.
        // Giả lập query đơn giản:
        
        return com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse.builder()
                .balance(wallet.getBalance())
                .todayIncome(BigDecimal.ZERO) // TODO: Implement query
                .weekIncome(BigDecimal.ZERO)
                .monthIncome(BigDecimal.ZERO)
                .build();
    }
}