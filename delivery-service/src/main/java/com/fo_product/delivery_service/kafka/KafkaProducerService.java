package com.fo_product.delivery_service.kafka;

import com.fo_product.delivery_service.kafka.events.ShipperFoundEvent;
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

    public void sendShipperFoundEvent(ShipperFoundEvent event) {
        log.info("Gửi yêu cầu nhận đơn cho Shipper ID: {}", event.shipperId());
        Message<ShipperFoundEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "shipper-found-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
