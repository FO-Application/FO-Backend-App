package com.fo_product.merchant_service.services.interfaces.wallet;

import com.fo_product.merchant_service.dtos.responses.wallet.DailyStatResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.models.enums.TransactionType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface IWalletService {
    void createWallet(Long restaurantId);
    WalletResponse getMyWallet(Long restaurantId);
    Page<WalletTransactionResponse> getMyTransactions(Long restaurantId, int page, int size, Instant startDate, Instant endDate, TransactionType type);
    byte[] exportTransactions(Long restaurantId, Instant startDate, Instant endDate, TransactionType type);
    List<DailyStatResponse> getDailyStatistics(Long restaurantId);
    WalletResponse withdraw(Long restaurantId, BigDecimal amount);
    WalletResponse deposit(Long restaurantId, BigDecimal amount);
}
