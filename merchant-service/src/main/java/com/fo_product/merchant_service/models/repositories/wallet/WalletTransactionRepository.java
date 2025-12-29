package com.fo_product.merchant_service.models.repositories.wallet;

import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
}
