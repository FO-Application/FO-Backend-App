package com.fo_product.order_service.kafka.events;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderCreatedEvent(
        Long orderId,
        Long merchantId,
        String merchantName,
        BigDecimal grandTotal,
        LocalDateTime createdAt,
        List<String> itemNames,
        String customerName
) {
}