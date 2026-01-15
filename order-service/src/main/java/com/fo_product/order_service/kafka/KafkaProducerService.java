package com.fo_product.order_service.kafka;

import com.fo_product.order_service.kafka.events.*;
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
        Message<OrderCompletedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-completed-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
        log.info("Bắn sự kiện tìm tài xế cho đơn hàng: {}", event.orderId());

        Message<OrderConfirmedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-confirmed-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendOrderDeliveringEvent(OrderDeliveringEvent event) {
        log.info("Order Service: Bắn tin thông báo Giao Hàng cho user {}", event.customerName());

        Message<OrderDeliveringEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-delivering-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Order Service: Bắn sự kiện CÓ ĐƠN MỚI cho quán ID {}", event.merchantId());

        Message<OrderCreatedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-created-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendOrderPaidEvent(OrderPaidEvent event) {
        log.info("Order Service: Bắn sự kiện thanh toán thành công MỚI cho đơn có mã ID {}", event.getOrderId());
        Message<OrderPaidEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-paid-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Order Service: Bắn sự kiện HỦY ĐƠN {} do {}", event.getOrderId(), event.getCancelledBy());
        Message<OrderCancelledEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-cancelled-topic")
                .build();
        kafkaTemplate.send(message);
    }

    public void sendOrderReadyEvent(OrderReadyEvent event) {
        log.info("Order Service: Bắn sự kiện MÓN ĐÃ XONG (READY) cho đơn {}", event.getOrderId());
        Message<OrderReadyEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-ready-topic")
                .build();
        kafkaTemplate.send(message);
    }

    public void sendReviewCreatedEvent(ReviewCreatedEvent event) {
        log.info("Order Service: Bắn sự kiện đánh giá đơn hàng ĐÃ XONG (READY) cho nhà hàng có ID {}", event.getMerchantId());
        Message<ReviewCreatedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "review-created-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
