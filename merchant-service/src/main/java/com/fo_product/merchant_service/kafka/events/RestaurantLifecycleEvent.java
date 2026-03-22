package com.fo_product.merchant_service.kafka.events;

import lombok.Builder;

@Builder
public record RestaurantLifecycleEvent(
        Long restaurantId,
        String restaurantName,
        String ownerEmail,
        String action
) {}
