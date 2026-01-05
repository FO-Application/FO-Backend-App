package com.fo_product.notification_service.dtos.request;

public record RegisterTokenRequest(
        Long userId,
        String fcmToken,
        String deviceType
) {
}
