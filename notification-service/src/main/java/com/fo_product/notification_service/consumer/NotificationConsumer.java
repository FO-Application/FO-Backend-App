package com.fo_product.notification_service.consumer;

import com.fo_product.notification_service.events.MailSenderEvent;
import com.fo_product.notification_service.services.interfaces.IMailSenderService;
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

    @KafkaListener(topics = "notification_topic", groupId = "notification-service-group")
    public void listen(MailSenderEvent event) {
        log.info("Received message: {}", event);

        if ("REGISTER".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else if("FORGOT_PASSWORD".equals(event.eventType())) {
            mailSenderService.sendOtpEmail(event.recipientEmail(), event.otpCode(), event.subject());
        } else {
            log.error("event type not valid");
        }
    }
}
