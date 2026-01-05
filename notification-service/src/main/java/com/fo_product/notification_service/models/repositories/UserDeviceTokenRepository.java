package com.fo_product.notification_service.models.repositories;

import com.fo_product.notification_service.models.entities.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken,Long> {
    List<UserDeviceToken> findByUserId(Long userId);
    Optional<UserDeviceToken> findByUserIdAndFcmToken(Long userId, String fcmToken);
}
