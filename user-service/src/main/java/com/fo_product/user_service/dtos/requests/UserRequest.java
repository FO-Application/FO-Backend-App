package com.fo_product.user_service.dtos.requests;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserRequest(
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email,

        @NotBlank(message = "NOT_BLANK")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,16}$",
                message = "INVALID_PASSWORD")
        String password,

        @NotBlank(message = "NOT_BLANK")
        String firstName,

        @NotBlank(message = "NOT_BLANK")
        String lastName,

        @NotBlank(message = "NOT_BLANK")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,

        @NotNull(message = "NOT_NULL")
        LocalDate dob
) {
}
