package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RestaurantScheduleMapper {

    public RestaurantScheduleResponse response(RestaurantSchedule restaurantSchedule) {
        Restaurant restaurant = restaurantSchedule.getRestaurant();
        if (restaurant == null)
            throw new MerchantException(MerchantExceptionCode.RESTAURANT_NOT_EXIST);

        return RestaurantScheduleResponse.builder()
                .id(restaurantSchedule.getId())
                .dayOfWeek(restaurantSchedule.getDayOfWeek())
                .openTime(restaurantSchedule.getOpenTime())
                .closeTime(restaurantSchedule.getCloseTime())
                .restaurantResponse(RestaurantResponse.builder()
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
                                        .imageFileUrl(cuisine.getImageFileUrl()
                                        ).build()
                        ).collect(Collectors.toSet()))
                        .build()
                )
                .build();
    }
}
