package com.fo_product.merchant_service.mappers.product;

import com.fo_product.merchant_service.dtos.responses.addon.OptionGroupResponse;
import com.fo_product.merchant_service.dtos.responses.addon.OptionItemResponse;
import com.fo_product.merchant_service.dtos.responses.product.ProductResponse;
import com.fo_product.merchant_service.models.entities.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse response(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .imageUrl(product.getImageUrl())
                .status(product.isStatus())
                .categoryName(product.getCategory().getName())
                .optionGroups(product.getOptionGroups().stream().map(
                        optionGroup -> OptionGroupResponse.builder()
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
                                                .build())
                                        .toList()
                                ).build())
                        .toList())
                .build();
    }
}
