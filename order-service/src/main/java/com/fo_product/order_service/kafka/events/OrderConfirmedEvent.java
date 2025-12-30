package com.fo_product.order_service.kafka.events;

import lombok.Builder;

@Builder
public record OrderConfirmedEvent(
        Long orderId,
        Long merchantId,
        String customerName,
        String customerPhone,
        String deliveryAddress
) {
}
