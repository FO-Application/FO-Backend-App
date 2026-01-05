package com.fo_product.notification_service.services.interfaces;

public interface INotificationService {
    void registerToken(Long userId, String fcmToken, String deviceType);
    void sendNotification(Long userId, String title, String body, Long orderId);
}
