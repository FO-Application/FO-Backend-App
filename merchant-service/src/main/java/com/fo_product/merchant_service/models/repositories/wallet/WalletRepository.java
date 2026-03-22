package com.fo_product.merchant_service.models.repositories.wallet;

import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByRestaurant_Id(Long restaurantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.restaurant.id = :restaurantId")
    Optional<Wallet> findLockedByRestaurant_Id(@Param("restaurantId") Long restaurantId);
}
