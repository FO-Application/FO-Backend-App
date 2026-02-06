package com.fo_product.notification_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.notification_service.dtos.request.RegisterTokenRequest;
import com.fo_product.notification_service.dtos.response.NotificationResponse;
import com.fo_product.notification_service.services.interfaces.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification Controller", description = "API quản lý thông báo")
public class NotificationController {
    INotificationService notificationService;

    // Frontend gọi API này ngay sau khi Login thành công
    @PostMapping("/device-token")
    @Operation(summary = "Đăng ký FCM token cho thiết bị")
    public APIResponse<?> registerToken(@RequestBody RegisterTokenRequest request) {
        notificationService.registerToken(request);
        return APIResponse.builder()
                .message("Success")
                .build();
    }

    // ========== NOTIFICATION HISTORY ==========

    @GetMapping("/history/{merchantId}")
    @Operation(summary = "Lấy lịch sử thông báo của merchant")
    public APIResponse<List<NotificationResponse>> getNotificationHistory(@PathVariable Long merchantId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByMerchant(merchantId);
        return APIResponse.<List<NotificationResponse>>builder()
                .result(notifications)
                .message("Success")
                .build();
    }

    // ========== MARK AS READ ==========

    @PutMapping("/{id}/read")
    @Operation(summary = "Đánh dấu một thông báo là đã đọc")
    public APIResponse<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return APIResponse.builder()
                .message("Notification marked as read")
                .build();
    }

    @PutMapping("/read-all/{merchantId}")
    @Operation(summary = "Đánh dấu tất cả thông báo là đã đọc")
    public APIResponse<?> markAllAsRead(@PathVariable Long merchantId) {
        notificationService.markAllAsRead(merchantId);
        return APIResponse.builder()
                .message("All notifications marked as read")
                .build();
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa một thông báo")
    public APIResponse<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return APIResponse.builder()
                .message("Notification deleted")
                .build();
    }

    @DeleteMapping("/delete-all/{merchantId}")
    @Operation(summary = "Xóa tất cả thông báo của merchant")
    public APIResponse<?> deleteAllNotifications(@PathVariable Long merchantId) {
        notificationService.deleteAllNotifications(merchantId);
        return APIResponse.builder()
                .message("All notifications deleted")
                .build();
    }
}

