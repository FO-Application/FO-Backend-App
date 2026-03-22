package com.fo_product.merchant_service.services.interfaces.wallet;

import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.models.enums.TransactionType;
import org.springframework.data.domain.Page;
import java.time.Instant;

public interface IAdminWalletService {
    Page<WalletTransactionResponse> getAllTransactions(int page, int size, Instant startDate, Instant endDate, TransactionType type);
}
