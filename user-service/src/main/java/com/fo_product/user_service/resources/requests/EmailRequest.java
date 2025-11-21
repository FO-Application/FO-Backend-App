package com.fo_product.user_service.resources.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "NOT_BLANK")
        String email
) {
}
