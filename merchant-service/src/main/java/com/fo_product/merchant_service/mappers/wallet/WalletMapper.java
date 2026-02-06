package com.fo_product.merchant_service.mappers.wallet;

import com.fo_product.merchant_service.dtos.responses.wallet.WalletResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletResponse response(Wallet wallet) {
        return WalletResponse.builder()
                .balance(wallet.getBalance())
                .currency("VND")
                .build();
    }

    public WalletTransactionResponse response(WalletTransaction transaction) {
        return WalletTransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .orderId(transaction.getOrderId())
                .build();
    }
}
