package com.fo_product.merchant_service.kafka;

import com.fo_product.merchant_service.kafka.events.MailSenderEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMailNotification(MailSenderEvent event) {
        log.info("Merchant service: Firing email event to {}", event.recipientEmail());
        Message<MailSenderEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "otp-mail-sender-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendRestaurantLifecycleEvent(com.fo_product.merchant_service.kafka.events.RestaurantLifecycleEvent event) {
        log.info("Merchant service: Firing RestaurantLifecycleEvent for {}", event.restaurantName());
        Message<com.fo_product.merchant_service.kafka.events.RestaurantLifecycleEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "restaurant-lifecycle-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
