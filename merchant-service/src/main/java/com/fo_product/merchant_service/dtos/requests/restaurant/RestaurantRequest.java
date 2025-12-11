package com.fo_product.merchant_service.dtos.requests.restaurant;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import jakarta.validation.constraints.NotBlank;

public record RestaurantRequest(
        @NotBlank(message = "NOT_BLANK")
        String name,

        @NotBlank(message = "NOT_BLANK")
        Long ownerId,

        String description,

        String slug,

        @NotBlank(message = "NOT_BLANK")
        String address,

        @NotBlank(message = "NOT_BLANK")
        Double latitude,

        @NotBlank(message = "NOT_BLANK")
        Double longitude,

        @NotBlank(message = "NOT_BLANK")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone
) {
}
