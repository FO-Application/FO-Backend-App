package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.CategoryResponse;
import com.fo_product.merchant_service.models.entities.product.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse response(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.isActive())
                .restaurantName(category.getRestaurant().getName())
                .build();
    }
}
