package com.fo_product.notification_service.consumer;

import com.fo_product.notification_service.events.MailSenderEvent;
import com.fo_product.notification_service.events.OrderCreatedEvent;
import com.fo_product.notification_service.events.OrderDeliveringEvent;
import com.fo_product.notification_service.events.ShipperFoundEvent;
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

    @KafkaListener(topics = "otp-mail-sender-topic", groupId = "notification-service-group")
    public void sendAuthMail(MailSenderEvent event) {
        log.info("Received message: {}", event);

        if ("REGISTER".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else if("FORGOT_PASSWORD".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else {
            log.error("event type not valid");
        }
    }

    @KafkaListener(topics = "order-delivering-topic", groupId = "notification-service-group")
    public void sendOrderDeliverMail(OrderDeliveringEvent event) {
        log.info("Received message: {}", event);

        mailSenderService.sendDeliverMail(event);
    }

    @KafkaListener(topics = "order-created-topic", groupId = "notification-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        notificationService.sendNotification(
                event.merchantId(),
                "Đơn hàng mới # " + event.orderId(),
                "Tổng tiền: " + event.grandTotal(),
                event.orderId()
        );
    }

    @KafkaListener(topics = "shipper-found-topic", groupId = "notification-group")
    public void handleShipperFound(ShipperFoundEvent event) {
        log.info("Bắn FCM mời nhận đơn cho Shipper ID: {}", event.shipperId());

        String title = "Có đơn hàng mới gần bạn!";
        String body = "Đơn #" + event.orderId() + " - Phí ship: " + event.shippingFee();

        // Gửi thông báo xuống App
        notificationService.sendNotification(
                event.shipperId(),
                title,
                body,
                event.orderId()
        );
    }
}
