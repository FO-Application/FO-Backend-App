package com.fo_product.merchant_service.models.repositories.wallet;

import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.Instant;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long>, JpaSpecificationExecutor<WalletTransaction> {
    List<WalletTransaction> findAllByWalletAndCreatedAtAfter(Wallet wallet, LocalDateTime createdAt);
}
