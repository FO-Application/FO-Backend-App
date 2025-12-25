package com.fo_product.merchant_service.mappers.restaurant;

import com.fo_product.merchant_service.dtos.responses.restaurant.RestaurantResponse;
import com.fo_product.merchant_service.models.entities.restaurant.Cuisine;
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
                .cuisineNames(restaurant.getCuisines().stream()
                        .map(Cuisine::getName)
                        .collect(Collectors.toSet())
                ).build();

    }
}
