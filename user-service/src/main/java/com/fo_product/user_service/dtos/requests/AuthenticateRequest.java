package com.fo_product.user_service.dtos.requests;

public record AuthenticateRequest(
        String email,
        String password
) { }
