package com.fo_product.merchant_service.dtos.responses;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OptionItemResponse(
        Long id,
        String name,
        BigDecimal priceAdjustment,
        boolean isAvailable,
        String optionGroupName
) {
}
