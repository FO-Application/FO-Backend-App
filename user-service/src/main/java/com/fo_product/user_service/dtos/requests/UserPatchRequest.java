package com.fo_product.user_service.dtos.requests;

import com.fo_product.common_lib.exceptions.validation.constraint.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UserPatchRequest(
        @Schema(description = "Địa chỉ Email", example = "nguyenvana01@gmail.com")
        @Email(message = "INVALID_EMAIL")
        String email,

        @Schema(description = "Tên (First Name)", example = "An")
        String firstName,

        @Schema(description = "Họ (Last Name)", example = "Nguyen")
        String lastName,

        @Schema(description = "Số điện thoại (10 số)", example = "0987654321")
        @PhoneConstraint(length = 10, message = "INVALID_PHONE")
        String phone,

        @Schema(description = "Ngày sinh (yyyy-MM-dd)", example = "2000-01-01")
        LocalDate dob
) {
}