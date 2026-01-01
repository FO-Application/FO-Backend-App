package com.fo_product.order_service.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderDeliveringEvent(
        Long orderId,
        String customerName,
        String customerEmail,
        String deliveryAddress,
        String merchantName,
        String descriptionOrder,
        List<String> productName,
        BigDecimal productPrice,
        BigDecimal shippingFee
) {
}
