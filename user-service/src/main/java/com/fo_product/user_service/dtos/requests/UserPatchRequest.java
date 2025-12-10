package com.fo_product.user_service.dtos.requests;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UserPatchRequest(
        @Email(message = "INVALID_EMAIL")
        String email,
        String firstName,
        String lastName,
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,
        LocalDate dob
) {
}
