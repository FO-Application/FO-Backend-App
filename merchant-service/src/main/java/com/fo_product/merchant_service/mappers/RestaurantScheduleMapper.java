package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantExceptionCode;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import org.springframework.stereotype.Component;

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
                        .build()
                )
                .build();
    }
}
