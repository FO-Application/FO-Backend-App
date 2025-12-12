package com.fo_product.user_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record UserResponse (
        @Schema(description = "ID định danh", example = "101")
        Long id,

        @Schema(description = "Email người dùng", example = "nguyenvana01@gmail.com")
        String email,

        @Schema(description = "Tên", example = "Minh")
        String firstName,

        @Schema(description = "Họ", example = "Le")
        String lastName,

        @Schema(description = "Ngày sinh", example = "1999-12-30")
        LocalDate dob,

        @Schema(description = "Số điện thoại", example = "0912345678")
        String phone,

        @Schema(description = "Vai trò trong hệ thống", example = "USER")
        String role
) implements Serializable {
}