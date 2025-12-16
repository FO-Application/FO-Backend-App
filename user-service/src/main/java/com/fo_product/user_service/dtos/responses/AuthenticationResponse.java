package com.fo_product.user_service.dtos.responses;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String role,
        boolean authenticated
) {
}
