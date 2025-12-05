package com.fo_product.user_service.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email,

        @NotBlank(message = "NOT_BLANK")
        String otpCode
) {
}
