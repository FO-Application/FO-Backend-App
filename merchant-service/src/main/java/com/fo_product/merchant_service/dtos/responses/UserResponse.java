package com.fo_product.merchant_service.dtos.responses;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        LocalDate dob
) {
}
