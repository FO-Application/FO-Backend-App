package com.fo_product.user_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Yêu cầu đặt lại mật khẩu mới (Reset Password Flow)")
public record NewPasswordRequest(

        @Schema(
                description = "Email của tài khoản cần đổi mật khẩu",
                example = "abc123@gmail.com"
        )
        @NotNull(message = "NOT_NULL")
        String email,

        @Schema(
                description = "Mật khẩu mới. Phải từ 8-16 ký tự, chứa ít nhất 1 chữ hoa, 1 thường, 1 số và 1 ký tự đặc biệt.",
                example = "Dinh@2025"
        )
        @NotBlank(message = "NOT_BLANK")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,16}$",
                message = "INVALID_PASSWORD")
        String newPassword,

        @Schema(
                description = "Mã OTP xác thực gồm 6 chữ số gửi về email",
                example = "123456"
        )
        @NotNull(message = "NOT_NULL")
        String otp
) {
}