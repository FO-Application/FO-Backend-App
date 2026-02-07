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
    WalletResponse getMyWallet();
    Page<WalletTransactionResponse> getMyTransactions(int page, int size, Instant startDate, Instant endDate, TransactionType type);
    byte[] exportTransactions(Instant startDate, Instant endDate, TransactionType type);
    List<DailyStatResponse> getDailyStatistics();
    WalletResponse withdraw(BigDecimal amount);
    
    WalletResponse deposit(BigDecimal amount);
}
