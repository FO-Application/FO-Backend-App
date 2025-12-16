package com.fo_product.merchant_service.dtos.requests.option_item;

public record OptionItemPatchRequest(
        String name,
        Long priceAdjustment,
        Boolean isAvailable,
        long optionGroupId
) {
}
