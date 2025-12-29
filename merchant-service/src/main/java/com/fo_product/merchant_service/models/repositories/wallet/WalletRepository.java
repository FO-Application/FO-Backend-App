package com.fo_product.merchant_service.models.repositories.wallet;

import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByRestaurant_Id(Long restaurantId);
}
