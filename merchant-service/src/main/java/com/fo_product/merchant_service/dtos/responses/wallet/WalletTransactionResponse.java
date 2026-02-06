package com.fo_product.merchant_service.dtos.responses.wallet;

import com.fo_product.merchant_service.models.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime createdAt;
    private Long orderId;
}
