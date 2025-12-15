package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.ProductResponse;
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
                .build();
    }
}
