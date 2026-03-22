package com.fo_product.order_service.kafka;

import com.fo_product.order_service.kafka.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    // --- XÓA BỎ HÀM buildMessageWithTypeHeader ---
    // Để Spring tự động thêm header dựa trên config YAML "add.type.headers: true"

    public void sendOrderCompletedEvent(OrderCompletedEvent event) {
        log.info("Order service: Xếp hàng event OrderCompleted {}", event.orderId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderCompleted(OrderCompletedEvent event) {
        kafkaTemplate.send("order-completed-topic", event);
        log.info("Thực sự bắn Kafka event OrderCompleted: {}", event.orderId());
    }

    public void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
        log.info("Xếp hàng event OrderConfirmed cho đơn hàng: {}", event.orderId());
        applicationEventPublisher.publishEvent(event);
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        kafkaTemplate.send("order-confirmed-topic", event);
        log.info("Thực sự bắn Kafka event OrderConfirmed: {}", event.orderId());
    }

    public void sendOrderDeliveringEvent(OrderDeliveringEvent event) {
        log.info("Xếp hàng event OrderDelivering cho user {}", event.customerName());
        applicationEventPublisher.publishEvent(event);
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderDelivering(OrderDeliveringEvent event) {
        kafkaTemplate.send("order-delivering-topic", event);
        log.info("Thực sự bắn Kafka event OrderDelivering: {}", event.orderId());
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Xếp hàng event OrderCreated cho quán ID {}", event.merchantId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order-created-topic", event);
        log.info("Thực sự bắn Kafka event OrderCreated: {}", event.orderId());
    }

    public void sendOrderPaidEvent(OrderPaidEvent event) {
        log.info("Xếp hàng event OrderPaid cho đơn ID {}", event.getOrderId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderPaid(OrderPaidEvent event) {
        kafkaTemplate.send("order-paid-topic", event);
        log.info("Thực sự bắn Kafka event OrderPaid: {}", event.getOrderId());
    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Xếp hàng event OrderCancelled {} do {}", event.getOrderId(), event.getCancelledBy());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderCancelled(OrderCancelledEvent event) {
        kafkaTemplate.send("order-cancelled-topic", event);
        log.info("Thực sự bắn Kafka event OrderCancelled: {}", event.getOrderId());
    }

    public void sendOrderReadyEvent(OrderReadyEvent event) {
        log.info("Xếp hàng event OrderReady cho đơn {}", event.getOrderId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderReady(OrderReadyEvent event) {
        kafkaTemplate.send("order-ready-topic", event);
        log.info("Thực sự bắn Kafka event OrderReady: {}", event.getOrderId());
    }

    public void sendReviewCreatedEvent(ReviewCreatedEvent event) {
        log.info("Xếp hàng event ReviewCreated cho nhà hàng ID {}", event.getMerchantId());
        applicationEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onReviewCreated(ReviewCreatedEvent event) {
        kafkaTemplate.send("review-created-topic", event);
        log.info("Thực sự bắn Kafka event ReviewCreated: {}", event.getMerchantId());
    }
}