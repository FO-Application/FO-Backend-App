package com.fo_product.user_service.resources.responses;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

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
