package com.fo_product.merchant_service.dtos.responses;

import lombok.Builder;

@Builder
public record RestaurantResponse(
        Long id,
        String name,
        UserResponse user,
        String slug,
        String address,
        Double latitude,
        Double longitude,
        String phone,
        boolean isActive,
        Double ratingAverage,
        int reviewCount,
        String imageFileUrl,
        String description
    ) {
}
