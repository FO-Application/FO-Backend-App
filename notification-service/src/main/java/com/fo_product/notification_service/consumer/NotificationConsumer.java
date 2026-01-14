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
    // ObjectMapper objectMapper; // Không cần dùng cái này nữa vì Spring tự parse rồi

    // --- 1. EMAIL OTP ---
    @KafkaListener(topics = "otp-mail-sender-topic", groupId = "notification-service-group")
    public void sendAuthMail(MailSenderEvent event) {
        log.info("Received message: {}", event);

        if ("REGISTER".equals(event.eventType()) || "FORGOT_PASSWORD".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else {
            log.error("event type not valid");
        }
    }

    // --- 2. EMAIL ĐANG GIAO HÀNG ---
    @KafkaListener(topics = "order-delivering-topic", groupId = "notification-service-group")
    public void sendOrderDeliverMail(OrderDeliveringEvent event) {
        log.info("Received message: {}", event);
        mailSenderService.sendDeliverMail(event);
    }

    // --- 3. BÁO CHỦ QUÁN: CÓ ĐƠN MỚI ---
    @KafkaListener(topics = "order-created-topic", groupId = "notification-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Gửi qua Topic Merchant để đảm bảo chủ quán nhận được
        notificationService.sendNotificationToTopic(
                "merchant-orders-" + event.merchantId(),
                "Đơn hàng mới #" + event.orderId(),
                "Tổng tiền: " + event.grandTotal(),
                event.orderId()
        );
    }

    // --- 4. BÁO SHIPPER: CÓ ĐƠN MỚI (NỔ ĐƠN) ---
    @KafkaListener(topics = "shipper-found-topic", groupId = "notification-group")
    public void handleShipperFound(ShipperFoundEvent event) {
        log.info("Bắn FCM mời nhận đơn cho Shipper ID: {}", event.shipperId());

        String title = "Có đơn hàng mới gần bạn!";
        String body = "Đơn #" + event.orderId() + " - Phí ship: " + event.shippingFee();

        notificationService.sendNotification(
                event.shipperId(),
                title,
                body,
                event.orderId()
        );
    }

    // --- 5. BÁO CHỦ QUÁN: TIỀN VỀ (PAID) ---
    @KafkaListener(topics = "order-paid-topic", groupId = "notification-group")
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
    @KafkaListener(topics = "shipper-assigned-topic", groupId = "notification-group")
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
    @KafkaListener(topics = "order-ready-topic", groupId = "notification-group")
    public void handleOrderReady(OrderReadyEvent event) { // Pass thẳng Object
        log.info("Order Ready: {}", event);

        if (event.getShipperId() != null) {
            notificationService.sendNotification(
                    event.getShipperId(),
                    "Món ăn đã sẵn sàng!",
                    "Quán đã nấu xong đơn #" + event.getOrderId() + ". Vào lấy ngay!",
                    event.getOrderId()
            );
        }
    }

    // --- 8. BÁO HỦY ĐƠN (CANCELLED) ---
    @KafkaListener(topics = "order-cancelled-topic", groupId = "notification-group")
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
}