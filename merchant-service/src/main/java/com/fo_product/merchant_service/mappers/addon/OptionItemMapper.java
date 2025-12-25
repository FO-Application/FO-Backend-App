package com.fo_product.merchant_service.mappers.addon;

import com.fo_product.merchant_service.dtos.responses.addon.OptionItemResponse;
import com.fo_product.merchant_service.models.entities.addon.OptionItem;
import org.springframework.stereotype.Component;

@Component
public class OptionItemMapper {
    public OptionItemResponse response(OptionItem optionItem) {
        return OptionItemResponse.builder()
                .id(optionItem.getId())
                .name(optionItem.getName())
                .priceAdjustment(optionItem.getPriceAdjustment())
                .isAvailable(optionItem.isAvailable())
                .optionGroupName(optionItem.getOptionGroup().getName())
                .build();
    }
}
