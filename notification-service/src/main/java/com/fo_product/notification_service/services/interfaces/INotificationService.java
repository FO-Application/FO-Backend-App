package com.fo_product.notification_service.services.interfaces;

import com.fo_product.notification_service.dtos.request.RegisterTokenRequest;
import com.fo_product.notification_service.dtos.response.NotificationResponse;

import java.util.List;

public interface INotificationService {
    void registerToken(RegisterTokenRequest request);
    void subscribeTopic(RegisterTokenRequest request);
    void sendNotification(Long userId, String title, String body, Long orderId, java.util.Map<String, String> data);
    void sendNotificationToTopic(String topic, String title, String body, Long orderId);
    
    // History & Management
    List<NotificationResponse> getNotificationsByMerchant(Long merchantId);
    List<NotificationResponse> getNotificationsByTopic(String topic);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long merchantId);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications(Long merchantId);
}
