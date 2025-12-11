package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
import org.springframework.stereotype.Component;

@Component
public class CuisineMapper {
    public CuisineResponse response (Cuisine cuisine) {
        return CuisineResponse.builder()
                .id(cuisine.getId())
                .name(cuisine.getName())
                .slug(cuisine.getSlug())
                .imageFileUrl(cuisine.getImageFileUrl())
                .build();
    }
}
