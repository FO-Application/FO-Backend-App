package com.fo_product.delivery_service.kafka;

import com.fo_product.delivery_service.kafka.events.ShipperAssignedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;
    ApplicationEventPublisher applicationEventPublisher;

    public void sendShipperFoundEvent(ShipperFoundEvent event) {
        log.info("Xếp hàng yêu cầu nhận đơn cho Shipper ID: {}", event.shipperId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onShipperFound(ShipperFoundEvent event) {
        Message<ShipperFoundEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "shipper-found-topic")
                .build();
        kafkaTemplate.send(message);
        log.info("Thực sự bắn Kafka báo có đơn cho Shipper ID: {}", event.shipperId());
    }

    public void sendShipperAssignedEvent(ShipperAssignedEvent event) {
        log.info("Xếp hàng event Shipper {} đã nhận đơn {}.", event.getShipperId(), event.getOrderId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onShipperAssigned(ShipperAssignedEvent event) {
        Message<ShipperAssignedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "shipper-assigned-topic")
                .build();
        kafkaTemplate.send(message);
        log.info("Thực sự bắn Kafka báo Shipper {} đã nhận đơn {}", event.getShipperId(), event.getOrderId());
    }
}
