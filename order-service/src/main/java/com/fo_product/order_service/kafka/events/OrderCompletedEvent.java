package com.fo_product.order_service.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCompletedEvent(
        Long orderId,
        Long merchantId,
        BigDecimal orderAmount
) {
}
