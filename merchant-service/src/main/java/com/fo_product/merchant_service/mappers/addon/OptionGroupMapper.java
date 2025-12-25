package com.fo_product.merchant_service.mappers.addon;

import com.fo_product.merchant_service.dtos.responses.addon.OptionGroupResponse;
import com.fo_product.merchant_service.dtos.responses.addon.OptionItemResponse;
import com.fo_product.merchant_service.models.entities.addon.OptionGroup;
import org.springframework.stereotype.Component;

@Component
public class OptionGroupMapper {
    public OptionGroupResponse response (OptionGroup optionGroup) {
        return OptionGroupResponse.builder()
                .id(optionGroup.getId())
                .name(optionGroup.getName())
                .isMandatory(optionGroup.isMandatory())
                .minSelection(optionGroup.getMinSelection())
                .maxSelection(optionGroup.getMaxSelection())
                .productName(optionGroup.getProduct().getName())
                .options(optionGroup.getOptionItems().stream().map(
                        optionItem -> OptionItemResponse.builder()
                                .id(optionItem.getId())
                                .name(optionItem.getName())
                                .isAvailable(optionItem.isAvailable())
                                .optionGroupName(optionItem.getOptionGroup().getName())
                                .build()
                        ).toList()
                ).build();
    }
}
