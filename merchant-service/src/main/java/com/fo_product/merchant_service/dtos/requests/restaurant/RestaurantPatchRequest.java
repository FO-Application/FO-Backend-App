package com.fo_product.merchant_service.dtos.requests.restaurant;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record RestaurantPatchRequest(
        String name,

        String description,

        Long ownerId,

        String slug,

        String address,

        Double latitude,

        Double longitude,

        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone
) {
}
