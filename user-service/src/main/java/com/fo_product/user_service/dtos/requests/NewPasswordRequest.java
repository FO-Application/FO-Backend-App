package com.fo_product.user_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NewPasswordRequest(
        @NotNull(message = "NOT_NULL")
        String email,
        @NotBlank(message = "NOT_BLANK")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,16}$",
                message = "INVALID_PASSWORD")
        String newPassword,
        @NotNull(message = "NOT_NULL")
        String otp
) {
}
