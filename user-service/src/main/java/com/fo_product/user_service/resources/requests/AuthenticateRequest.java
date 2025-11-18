package com.fo_product.user_service.resources.requests;

public record AuthenticateRequest(
        String email,
        String password
) { }
