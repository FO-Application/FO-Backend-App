package com.fo_product.user_service.dtos.responses;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PendingUserResponse(
        String email,
        String firstName,
        String lastName,
        LocalDate dob,
        String phone,
        String status
) {
}
