package com.fo_product.notification_service.consumer;

import com.fo_product.notification_service.events.*;
import com.fo_product.notification_service.services.interfaces.IMailSenderService;
import com.fo_product.notification_service.services.interfaces.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationConsumer {
    IMailSenderService mailSenderService;
    INotificationService notificationService;

    // --- 1. EMAIL OTP ---
    @KafkaListener(topics = "otp-mail-sender-topic", groupId = "notification-service-group-v3")
    public void sendAuthMail(MailSenderEvent event) {
        log.info("Received message: {}", event);

        if ("REGISTER".equals(event.eventType()) || "FORGOT_PASSWORD".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else {
            log.error("event type not valid");
        }
    }

    // --- 2. EMAIL ĐANG GIAO HÀNG ---
    @KafkaListener(topics = "order-delivering-topic", groupId = "notification-service-group-v3")
    public void sendOrderDeliverMail(OrderDeliveringEvent event) {
        log.info("Received message: {}", event);
        mailSenderService.sendDeliverMail(event);
    }

    // --- 3. BÁO CHỦ QUÁN: CÓ ĐƠN MỚI ---
    @KafkaListener(topics = "order-created-topic", groupId = "notification-service-group-v3")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📦 Nhận được sự kiện ĐƠN MỚI: orderId={}, merchantId={}", event.orderId(), event.merchantId());
        
        // Gửi qua Topic Merchant để đảm bảo chủ quán nhận được
        notificationService.sendNotificationToTopic(
                "merchant-orders-" + event.merchantId(),
                "Đơn hàng mới #" + event.orderId(),
                "Tổng tiền: " + event.grandTotal(),
                event.orderId()
        );
        
        log.info("✅ Đã gửi FCM tới topic merchant-orders-{}", event.merchantId());
    }

    // --- 4. BÁO SHIPPER: CÓ ĐƠN MỚI (NỔ ĐƠN) ---
    @KafkaListener(topics = "shipper-found-topic", groupId = "notification-service-group-v3")
    public void handleShipperFound(ShipperFoundEvent event) {
        log.info("Bắn FCM mời nhận đơn cho Shipper ID: {}", event.shipperId());

        String title = "Có đơn hàng mới gần bạn!";
        String body = "Đơn #" + event.orderId() + " - Phí ship: " + event.shippingFee();
        
        // Prepare extra data for Shipper App UI
        java.util.Map<String, String> data = new java.util.HashMap<>();
        data.put("pickupAddress", event.pickupAddress() != null ? event.pickupAddress() : "");
        data.put("shippingFee", event.shippingFee() != null ? event.shippingFee().toString() : "0");
        data.put("lat", event.lat() != null ? event.lat().toString() : "0");
        data.put("lon", event.lon() != null ? event.lon().toString() : "0");

        notificationService.sendNotification(
                event.shipperId(),
                title,
                body,
                event.orderId(),
                data
        );
    }

    // --- 5. BÁO CHỦ QUÁN: TIỀN VỀ (PAID) ---
    @KafkaListener(topics = "order-paid-topic", groupId = "notification-service-group-v3")
    public void handleOrderPaidEvent(OrderPaidEvent event) { // Pass thẳng Object
        log.info("Received Order Paid Event: {}", event);

        String title = "Đơn hàng #" + event.getOrderId() + " đã thanh toán";
        String body = "Đã nhận " + event.getAmount() + " qua " + event.getPaymentMethod();

        // Gửi qua Topic Merchant
        notificationService.sendNotificationToTopic(
                "merchant-orders-" + event.getMerchantId(),
                title,
                body,
                event.getOrderId()
        );
    }

    // --- 6. BÁO KHÁCH HÀNG: TÀI XẾ ĐÃ NHẬN (ASSIGNED) ---
    @KafkaListener(topics = "shipper-assigned-topic", groupId = "notification-service-group-v3")
    public void handleShipperAssigned(ShipperAssignedEvent event) { // Pass thẳng Object
        log.info("Shipper Assigned: {}", event);

        String title = "Tài xế đã nhận đơn!";
        String body = String.format("Tài xế %s (%s) đang đến quán.",
                event.getShipperName(), event.getLicensePlate());

        // Báo cho khách hàng qua Topic đơn hàng
        notificationService.sendNotificationToTopic(
                "order-" + event.getOrderId(),
                title,
                body,
                event.getOrderId()
        );
    }

    // --- 7. BÁO SHIPPER: MÓN ĐÃ XONG (READY) ---
    @KafkaListener(topics = "order-ready-topic", groupId = "notification-service-group-v3")
    public void handleOrderReady(OrderReadyEvent event) { // Pass thẳng Object
        log.info("Order Ready: {}", event);

        if (event.getShipperId() != null) {
            notificationService.sendNotification(
                    event.getShipperId(),
                    "Món ăn đã sẵn sàng!",
                    "Quán đã nấu xong đơn #" + event.getOrderId() + ". Vào lấy ngay!",
                    event.getOrderId(),
                    null // No extra data needed
            );
        }
    }

    // --- 8. BÁO HỦY ĐƠN (CANCELLED) ---
    @KafkaListener(topics = "order-cancelled-topic", groupId = "notification-service-group-v3")
    public void handleOrderCancelled(OrderCancelledEvent event) { // Pass thẳng Object
        log.info("Order Cancelled: {}", event);

        String title = "Đơn hàng #" + event.getOrderId() + " đã bị hủy";
        String body = "Lý do: " + event.getReason();

        if ("CUSTOMER".equals(event.getCancelledBy())) {
            // Khách hủy -> Báo Chủ Quán (Topic Merchant)
            notificationService.sendNotificationToTopic(
                    "merchant-orders-" + event.getMerchantId(),
                    title,
                    body,
                    event.getOrderId()
            );
        } else {
            // Quán hủy -> Báo Khách (Topic Order)
            notificationService.sendNotificationToTopic(
                    "order-" + event.getOrderId(),
                    title,
                    body,
                    event.getOrderId()
            );
        }
    }

    // --- 9. BÁO RÚT TIỀN THÀNH CÔNG (WITHDRAWAL) ---
    @KafkaListener(topics = "wallet-withdrawal-topic", groupId = "notification-service-group-v3")
    public void handleWalletWithdrawal(WalletWithdrawalEvent event) {
        log.info("Wallet Withdrawal Event: {}", event);

        String title = "Rút tiền thành công";
        String body = "Giao dịch #" + event.getTransactionId() + ": -" + event.getAmount() + " VNĐ đã được xử lý.";

        // Báo cho Merchant qua Topic riêng
        notificationService.sendNotificationToTopic(
                "merchant-wallet-" + event.getUserId(),
                title,
                body,
                event.getTransactionId()
        );
    }
}