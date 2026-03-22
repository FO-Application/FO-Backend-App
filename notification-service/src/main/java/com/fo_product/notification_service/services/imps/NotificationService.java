package com.fo_product.notification_service.services.imps;

import com.fo_product.notification_service.dtos.request.RegisterTokenRequest;
import com.fo_product.notification_service.dtos.response.NotificationResponse;
import com.fo_product.notification_service.models.entities.Notification;
import com.fo_product.notification_service.models.entities.UserDeviceToken;
import com.fo_product.notification_service.models.repositories.NotificationRepository;
import com.fo_product.notification_service.models.repositories.UserDeviceTokenRepository;
import com.fo_product.notification_service.services.interfaces.INotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class NotificationService implements INotificationService {
    UserDeviceTokenRepository userDeviceTokenRepository;
    NotificationRepository notificationRepository;

    @Override
    @Transactional
    //Hàm cho front end đăng ký token
    public void registerToken(RegisterTokenRequest request) {
        List<UserDeviceToken> existingTokens = userDeviceTokenRepository.findByUserIdAndFcmToken(request.userId(), request.fcmToken());

        if (existingTokens.isEmpty()) {
            UserDeviceToken userDeviceToken = UserDeviceToken.builder()
                    .userId(request.userId())
                    .fcmToken(request.fcmToken())
                    .deviceType(request.deviceType())
                    .build();

            userDeviceTokenRepository.save(userDeviceToken);
        } else if (existingTokens.size() > 1) {
            // Clean up duplicates: keep the first one, delete the rest
            log.warn("Found {} duplicate tokens for user {}. Cleaning up...", existingTokens.size(), request.userId());
            for (int i = 1; i < existingTokens.size(); i++) {
                userDeviceTokenRepository.delete(existingTokens.get(i));
            }
        }

        subscribeTopic(request);
    }

    @Override
    public void subscribeTopic(RegisterTokenRequest request) {
        try {
            if ("MERCHANT".equalsIgnoreCase(request.role()) && request.merchantId() != null) {
                String topicName = "merchant-orders-" + request.merchantId();

                FirebaseMessaging.getInstance().subscribeToTopic(
                        Collections.singletonList(request.fcmToken()),
                        topicName
                );
                log.info("MERCHANT {} đã subscribe vào topic: {}", request.userId(), topicName);
            }

        } catch (Exception e) {
            log.error("Lỗi khi subscribe topic: ", e);
        }
    }

    @Override
    public void sendNotification(Long userId, String title, String body, Long orderId, java.util.Map<String, String> data) {
        //B1: Lưu lịch sử vào CSDL
        Notification notification = Notification.builder()
                .recipientId(userId)
                .title(title)
                .message(body)
                .referenceId(orderId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        //B2: Lấy danh sách token của user này
        List<UserDeviceToken> userDeviceTokens = userDeviceTokenRepository.findByUserId(userId);
        if (userDeviceTokens.isEmpty()) return;

        //B3: Gửi Firebase cho từng token
        for (UserDeviceToken token : userDeviceTokens) {
            try {
                // Build base message
                var messageBuilder = Message.builder()
                        .setToken(token.getFcmToken())
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putData("orderId", String.valueOf(orderId));
                
                // Add extra data if present
                if (data != null) {
                    messageBuilder.putAllData(data);
                }

                Message message = messageBuilder.build();

                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                // Xử lý thông minh: Nếu token lỗi thì xóa luôn
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                        e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    log.warn("Token không tồn tại, đang xóa: {}", token.getFcmToken());
                    userDeviceTokenRepository.delete(token);
                } else {
                    log.error("Lỗi gửi FCM: ", e);
                }
            } catch (Exception e) {
                log.error("Lỗi hệ thống: ", e);
            }
        }
    }

    @Override
    public void sendNotificationToTopic(String topic, String title, String body, Long orderId) {
        try {
            // 1. Lưu log vào DB với topic để có thể query sau
            Notification notification = Notification.builder()
                    .recipientId(0L) // 0 đại diện cho Topic
                    .title(title)
                    .message(body)
                    .referenceId(orderId)
                    .topic(topic) // Lưu topic để query history
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);

            // 2. Gửi Firebase Topic
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("orderId", String.valueOf(orderId))
                    .putData("notificationId", String.valueOf(notification.getId()))
                    .build();

            FirebaseMessaging.getInstance().send(message);
            log.info("Đã gửi thông báo tới Topic: {}", topic);

        } catch (Exception e) {
            log.error("Lỗi gửi FCM Topic: ", e);
        }
    }

    // ========== HISTORY & MANAGEMENT METHODS ==========

    @Override
    public List<NotificationResponse> getNotificationsByMerchant(Long merchantId) {
        String topic = "merchant-orders-" + merchantId;
        List<Notification> notifications = notificationRepository.findByTopicOrderByCreatedAtDesc(topic);
        
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsByTopic(String topic) {
        List<Notification> notifications = notificationRepository.findByTopicOrderByCreatedAtDesc(topic);
        
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(Long merchantId) {
        String topic = "merchant-orders-" + merchantId;
        notificationRepository.markAllAsReadByTopic(topic);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long merchantId) {
        String topic = "merchant-orders-" + merchantId;
        notificationRepository.deleteByTopic(topic);
    }

    // ========== HELPER METHODS ==========

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .topic(notification.getTopic())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
