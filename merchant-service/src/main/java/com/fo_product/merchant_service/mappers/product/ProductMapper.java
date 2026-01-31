package com.fo_product.merchant_service.mappers.product;

import com.fo_product.merchant_service.dtos.responses.addon.OptionGroupResponse;
import com.fo_product.merchant_service.dtos.responses.addon.OptionItemResponse;
import com.fo_product.merchant_service.dtos.responses.product.ProductResponse;
import com.fo_product.merchant_service.models.entities.product.Product;
import org.springframework.stereotype.Component;

import java.util.Collections; // Nhớ import thư viện này

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
                // Lưu ý: Nếu product.getCategory() có thể null thì cũng nên check nốt ở đây
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)

                // --- BẮT ĐẦU SỬA: Check null an toàn cho OptionGroups ---
                .optionGroups(product.getOptionGroups() != null
                        ? product.getOptionGroups().stream().map(
                                optionGroup -> OptionGroupResponse.builder()
                                        .id(optionGroup.getId())
                                        .name(optionGroup.getName())
                                        .isMandatory(optionGroup.isMandatory())
                                        .minSelection(optionGroup.getMinSelection())
                                        .maxSelection(optionGroup.getMaxSelection())
                                        .productName(optionGroup.getProduct().getName())
                                        .options(optionGroup.getOptionItems() != null
                                                ? optionGroup.getOptionItems().stream().map(
                                                        optionItem -> OptionItemResponse.builder()
                                                                .id(optionItem.getId())
                                                                .name(optionItem.getName())
                                                                .isAvailable(optionItem.isAvailable())
                                                                .optionGroupName(optionItem.getOptionGroup().getName())
                                                                .build())
                                                .toList()
                                                : Collections.emptyList())
                                        .build())
                        .toList()
                        : Collections.emptyList())
                .build();
    }
}