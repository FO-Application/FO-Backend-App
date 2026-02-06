package com.fo_product.notification_service.models.repositories;

import com.fo_product.notification_service.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find all notifications for a topic, newest first
    List<Notification> findByTopicOrderByCreatedAtDesc(String topic);
    
    // Count unread notifications for a topic
    long countByTopicAndIsReadFalse(String topic);
    
    // Mark all as read for a topic
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.topic = :topic AND n.isRead = false")
    int markAllAsReadByTopic(String topic);
    
    // Delete all notifications for a topic
    void deleteByTopic(String topic);
}
