package com.fo_product.notification_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    Long id;
    String title;
    String message;
    Long referenceId;
    String topic;
    Boolean isRead;
    LocalDateTime createdAt;
}
