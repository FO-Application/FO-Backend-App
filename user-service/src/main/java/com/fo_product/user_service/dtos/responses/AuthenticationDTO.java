package com.fo_product.user_service.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthenticationDTO(
        @Schema(description = "Token dùng để truy cập API (ngắn hạn)", example = "eyJhbGciOiJIUz...")
        String accessToken,

        @Schema(description = "Token dùng để lấy lại AccessToken mới (dài hạn)", example = "eyJhbGciOiJIUz...")
        String refreshToken,

        @Schema(description = "Role của người dùng trả về", example = "ADMIN")
        String role
) {
}