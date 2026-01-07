package com.fo_product.user_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Yêu cầu đăng nhập thông qua Google")
public record GoogleLoginRequest(

        @Schema(
                description = "Google ID Token nhận được từ Client (Web/Mobile) sau khi đăng nhập Google thành công",
                example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjZm..."
        )
        String token
) {
}