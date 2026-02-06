package com.fo_product.delivery_service.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ShipperWalletResponse {
    BigDecimal balance;
    BigDecimal todayIncome;
    BigDecimal weekIncome;
    BigDecimal monthIncome;
}
