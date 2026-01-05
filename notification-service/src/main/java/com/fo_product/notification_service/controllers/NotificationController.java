package com.fo_product.notification_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.notification_service.dtos.request.RegisterTokenRequest;
import com.fo_product.notification_service.services.interfaces.INotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification Controller", description = "API gửi thông báo cho hệ thống")
public class NotificationController {
    INotificationService notificationService;

    // Frontend gọi API này ngay sau khi Login thành công
    @PostMapping("/device-token")
    public APIResponse<?> registerToken(@RequestBody RegisterTokenRequest request) {
        notificationService.registerToken(request.userId(), request.fcmToken(), request.deviceType());
        return APIResponse.builder()
                .message("Success")
                .build();
    }
}
