package com.fo_product.user_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "NOT_BLANK")
        String token
) {
}
