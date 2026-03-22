package com.fo_product.notification_service.events;

import lombok.Builder;

@Builder
public record RestaurantLifecycleEvent(
        Long restaurantId,
        String restaurantName,
        String ownerEmail,
        String action
) {}
