package com.fo_product.merchant_service.mappers;

import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;
import com.fo_product.merchant_service.models.entities.restaurant.RestaurantSchedule;
import org.springframework.stereotype.Component;

@Component
public class RestaurantScheduleMapper {

    public RestaurantScheduleResponse response(RestaurantSchedule restaurantSchedule) {
        return RestaurantScheduleResponse.builder()
                .id(restaurantSchedule.getId())
                .dayOfWeek(restaurantSchedule.getDayOfWeek())
                .openTime(restaurantSchedule.getOpenTime())
                .closeTime(restaurantSchedule.getCloseTime())
                .restaurantName(restaurantSchedule.getRestaurant().getName())
                .build();
    }
}
