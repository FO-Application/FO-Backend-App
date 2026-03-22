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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fo_product.merchant_service.models.enums.TransactionType;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long>, JpaSpecificationExecutor<WalletTransaction> {
    List<WalletTransaction> findAllByWalletAndCreatedAtAfter(Wallet wallet, LocalDateTime createdAt);
    
    boolean existsByOrderIdAndTransactionType(Long orderId, TransactionType transactionType);

    @Query(value = "SELECT " +
           "CAST(wt.created_at AS DATE) as statDate, " +
           "SUM(CASE WHEN wt.amount >= 0 THEN wt.amount ELSE 0 END) as income, " +
           "SUM(CASE WHEN wt.amount < 0 THEN ABS(wt.amount) ELSE 0 END) as expense " +
           "FROM wallet_transactions wt " +
           "WHERE wt.wallet_id = :walletId AND wt.created_at >= :startDate " +
           "GROUP BY CAST(wt.created_at AS DATE) ORDER BY statDate ASC", nativeQuery = true)
    List<Object[]> getDailyStatisticsNative(@Param("walletId") Long walletId, @Param("startDate") LocalDateTime startDate);
}
