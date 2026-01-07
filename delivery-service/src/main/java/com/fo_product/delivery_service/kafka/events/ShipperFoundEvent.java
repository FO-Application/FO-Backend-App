package com.fo_product.delivery_service.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShipperFoundEvent(
        Long shipperId,
        Long orderId,
        String pickupAddress,
        Double lat,
        Double lon,
        BigDecimal shippingFee
) {
}
