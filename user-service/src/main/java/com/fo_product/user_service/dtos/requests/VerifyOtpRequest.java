package com.fo_product.user_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @Schema(description = "Email xác thực", example = "nguyenvana01@gmail.com")
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email,

        @Schema(description = "Mã OTP 6 số", example = "123456")
        @NotBlank(message = "NOT_BLANK")
        String otpCode
) {
}