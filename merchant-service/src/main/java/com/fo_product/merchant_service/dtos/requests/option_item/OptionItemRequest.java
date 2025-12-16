package com.fo_product.merchant_service.dtos.requests.option_item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OptionItemRequest(
        @NotBlank(message = "NOT_BLANK")
        String name,

        @NotNull(message = "NOT_NULL")
        long priceAdjustment,

        @NotNull(message = "NOT_NULL")
        long optionGroupId
) {
}
