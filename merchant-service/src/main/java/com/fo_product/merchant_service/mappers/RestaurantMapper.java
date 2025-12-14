package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RestaurantMapper {
    public RestaurantResponse response(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .slug(restaurant.getSlug())
                .address(restaurant.getAddress())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .phone(restaurant.getPhone())
                .isActive(restaurant.isActive())
                .ratingAverage(restaurant.getRatingAverage())
                .reviewCount(restaurant.getReviewCount())
                .imageFileUrl(restaurant.getImageFileUrl())
                .description(restaurant.getDescription())
                .cuisines(restaurant.getCuisines().stream().map(
                        cuisine -> CuisineResponse.builder()
                                .id(cuisine.getId())
                                .name(cuisine.getName())
                                .slug(cuisine.getSlug())
                                .imageFileUrl(cuisine.getImageFileUrl())
                                .build()
                ).collect(Collectors.toSet()))
                .build();

    }
}
