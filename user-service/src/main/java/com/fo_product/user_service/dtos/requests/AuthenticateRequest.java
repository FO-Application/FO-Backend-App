package com.fo_product.user_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticateRequest(
        @Schema(description = "Email đăng nhập", example = "test_user@gmail.com")
        String email,

        @Schema(description = "Mật khẩu", example = "Password123@")
        String password
) { }