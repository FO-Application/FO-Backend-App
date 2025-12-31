package com.fo_product.user_service.kafka;

import com.fo_product.user_service.kafka.events.MailSenderEvent;
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
    KafkaTemplate<String, MailSenderEvent> kafkaTemplate;

    public void sendMailOTP(MailSenderEvent event) {
        log.info("User service: Bắn sự kiện gửi mã OTP {}", event.recipientEmail());
        Message<MailSenderEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "otp-mail-sender-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
