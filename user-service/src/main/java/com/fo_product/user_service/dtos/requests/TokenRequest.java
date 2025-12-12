package com.fo_product.user_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenRequest(
        @Schema(description = "Chuỗi Refresh Token cần xử lý", example = "eyJhbGciOiJIUzUxMiJ9...")
        String refreshToken
) {
}