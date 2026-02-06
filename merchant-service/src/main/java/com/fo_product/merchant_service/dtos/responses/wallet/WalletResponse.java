package com.fo_product.merchant_service.dtos.responses.wallet;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {
    private BigDecimal balance;
    private String currency; // "VND"
}
