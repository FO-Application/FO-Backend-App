package com.fo_product.delivery_service.kafka;

import com.fo_product.delivery_service.kafka.events.OrderConfirmedEvent;
import com.fo_product.delivery_service.services.imps.OrderMatchingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumerService {
    OrderMatchingService orderMatchingService;

    @KafkaListener(topics = "order-confirmed-topic", groupId = "delivery-service-group")
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {
        log.info("Kafka nhận đơn: {}", event.orderId());
        // Ủy quyền xử lý tìm kiếm
        orderMatchingService.processMatching(event);
    }
}
