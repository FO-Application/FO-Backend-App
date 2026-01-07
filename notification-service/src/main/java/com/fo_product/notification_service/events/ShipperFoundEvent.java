package com.fo_product.notification_service.events;

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
