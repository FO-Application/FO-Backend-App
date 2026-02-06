package com.fo_product.notification_service.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "recipient_id")
    Long recipientId;

    String title;
    String message;

    @Column(name = "reference_id")
    Long referenceId;

    @Column(name = "topic")
    String topic;

    @Column(name = "is_read")
    @Builder.Default
    Boolean isRead = false;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
