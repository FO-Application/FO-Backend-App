package com.fo_product.merchant_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record UserResponse(
        @Schema(description = "ID User", example = "101")
        Long id,

        @Schema(description = "Email", example = "nguyenvana01@gmail.com")
        String email,

        @Schema(description = "Tên", example = "Nam")
        String firstName,

        @Schema(description = "Họ", example = "Nguyen")
        String lastName,

        @Schema(description = "SĐT", example = "0987654321")
        String phone,

        @Schema(description = "Ngày sinh", example = "1990-01-01")
        LocalDate dob,

        @Schema(description = "Vai trò", example = "MERCHANT")
        String role
) implements Serializable {
}