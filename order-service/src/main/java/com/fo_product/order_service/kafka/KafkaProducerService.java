package com.fo_product.order_service.kafka;

import com.fo_product.order_service.kafka.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    // Value là Object để Spring tự serialize bất kỳ DTO nào
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // --- XÓA BỎ HÀM buildMessageWithTypeHeader ---
    // Để Spring tự động thêm header dựa trên config YAML "add.type.headers: true"

    public void sendOrderCompletedEvent(OrderCompletedEvent event) {
        log.info("Order service: Bắn sự kiện hoàn thành đơn hàng {}", event.orderId());
        kafkaTemplate.send("order-completed-topic", event);
    }

    public void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
        log.info("Bắn sự kiện tìm tài xế cho đơn hàng: {}", event.orderId());
        kafkaTemplate.send("order-confirmed-topic", event);
    }

    public void sendOrderDeliveringEvent(OrderDeliveringEvent event) {
        log.info("Order Service: Bắn tin thông báo Giao Hàng cho user {}", event.customerName());
        kafkaTemplate.send("order-delivering-topic", event);
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Order Service: Bắn sự kiện CÓ ĐƠN MỚI cho quán ID {}", event.merchantId());
        // QUAN TRỌNG: Gửi thẳng object event, không bọc trong MessageBuilder
        kafkaTemplate.send("order-created-topic", event);
    }

    public void sendOrderPaidEvent(OrderPaidEvent event) {
        log.info("Order Service: Bắn sự kiện thanh toán thành công MỚI cho đơn có mã ID {}", event.getOrderId());
        kafkaTemplate.send("order-paid-topic", event);
    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Order Service: Bắn sự kiện HỦY ĐƠN {} do {}", event.getOrderId(), event.getCancelledBy());
        kafkaTemplate.send("order-cancelled-topic", event);
    }

    public void sendOrderReadyEvent(OrderReadyEvent event) {
        log.info("Order Service: Bắn sự kiện MÓN ĐÃ XONG (READY) cho đơn {}", event.getOrderId());
        kafkaTemplate.send("order-ready-topic", event);
    }

    public void sendReviewCreatedEvent(ReviewCreatedEvent event) {
        log.info("Order Service: Bắn sự kiện đánh giá đơn hàng ĐÃ XONG (READY) cho nhà hàng có ID {}", event.getMerchantId());
        kafkaTemplate.send("review-created-topic", event);
    }
}