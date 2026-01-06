package com.fo_product.user_service.dtos.requests;

import com.fo_product.user_service.models.enums.OtpTokenType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(
        @Schema(description = "Email cần gửi lại OTP", example = "nguyenvana01@gmail.com")
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email,

        @Schema(description = "Thể loại cấp mã OTP, có 2 loại, 1 là  REGISTER, 2 là FORGOT_PASSWORD", example = "FORGOT_PASSWORD hoặc REGISTER")
        @NotNull(message = "NOT_NULL")
        OtpTokenType type
) {
}