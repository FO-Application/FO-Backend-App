package com.fo_product.user_service.kafka;

import com.fo_product.user_service.kafka.events.MailSenderEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, MailSenderEvent> kafkaTemplate;

    public void sendEvent(String topic, MailSenderEvent event) {
        kafkaTemplate.send(topic, event.recipientEmail(), event);
    }
}
