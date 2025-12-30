package com.fo_product.notification_service.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDeliveringEvent(
        Long orderId,
        String customerName,
        String customerEmail,
        String deliveryAddress,
        String merchantName,
        String descriptionOrder,
        String productName,
        BigDecimal amount
) {
}
