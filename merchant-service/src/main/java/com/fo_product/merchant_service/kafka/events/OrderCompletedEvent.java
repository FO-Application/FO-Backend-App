package com.fo_product.merchant_service.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCompletedEvent(
        Long orderId,
        Long merchantId,
        BigDecimal orderAmount
) {
}
