package com.fo_product.notification_service.services.interfaces;

import com.fo_product.notification_service.dtos.request.RegisterTokenRequest;

public interface INotificationService {
    void registerToken(RegisterTokenRequest request);
    void subscribeTopic(RegisterTokenRequest request);
    void sendNotification(Long userId, String title, String body, Long orderId);
    void sendNotificationToTopic(String topic, String title, String body, Long orderId);
}
