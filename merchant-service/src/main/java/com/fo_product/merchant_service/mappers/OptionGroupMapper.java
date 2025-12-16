package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.OptionGroupResponse;
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
                .build();
    }
}
