package com.fo_product.order_service.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderConfirmedEvent(
        Long orderId,
        Long merchantId,
        String customerName,
        String customerPhone,
        String deliveryAddress,
        BigDecimal productPrice,
        BigDecimal shippingFee
) {
}
