package com.fo_product.order_service.kafka;

import com.fo_product.order_service.kafka.events.OrderCompletedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCompletedEvent(OrderCompletedEvent event) {
        log.info("Order service: Bắn sự kiện hoàn thành đơn hàng {}", event.orderId());
        Message<OrderCompletedEvent> message = MessageBuilder.
                withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-completed-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
