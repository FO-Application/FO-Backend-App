package com.fo_product.notification_service.services.imps;

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

import java.util.List;

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
    public void registerToken(Long userId, String fcmToken, String deviceType) {
        var existingToken = userDeviceTokenRepository.findByUserIdAndFcmToken(userId, fcmToken);

        if (existingToken.isEmpty()) {
            UserDeviceToken userDeviceToken = UserDeviceToken.builder()
                    .userId(userId)
                    .fcmToken(fcmToken)
                    .deviceType(deviceType)
                    .build();

            userDeviceTokenRepository.save(userDeviceToken);
        }
    }

    @Override
    public void sendNotification(Long userId, String title, String body, Long orderId) {
        //B1: Lưu lịch sử vào CSDL
        Notification notification = Notification.builder()
                .recipientId(userId)
                .title(title)
                .message(body)
                .referenceId(orderId)
                .build();

        notificationRepository.save(notification);

        //B2: Lấy danh sách token của user này
        List<UserDeviceToken> userDeviceTokens = userDeviceTokenRepository.findByUserId(userId);
        if (userDeviceTokens.isEmpty()) return;

        //B3: Gửi Firebase cho từng token
        for (UserDeviceToken token : userDeviceTokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getFcmToken())
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putData("orderId", String.valueOf(orderId))
                        .build();

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
}
