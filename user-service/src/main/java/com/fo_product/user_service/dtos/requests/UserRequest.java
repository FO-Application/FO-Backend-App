package com.fo_product.user_service.dtos.requests;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserRequest(
        @Schema(description = "Email đăng ký", example = "nguyenvana01@gmail.com")
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email,

        @Schema(description = "Mật khẩu (8-16 ký tự, gồm chữ hoa, thường, số, ký tự đặc biệt)", example = "StrongP@ss01")
        @NotBlank(message = "NOT_BLANK")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,16}$",
                message = "INVALID_PASSWORD")
        String password,

        @Schema(description = "Tên", example = "Tung")
        @NotBlank(message = "NOT_BLANK")
        String firstName,

        @Schema(description = "Họ", example = "Son")
        @NotBlank(message = "NOT_BLANK")
        String lastName,

        @Schema(description = "Số điện thoại (10 số)", example = "0987654321")
        @NotBlank(message = "NOT_BLANK")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,

        @Schema(description = "Ngày sinh", example = "2000-01-01")
        @NotNull(message = "NOT_NULL")
        LocalDate dob
) {
}