package com.fo_product.merchant_service.services.interfaces.restaurant;

import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantSchedulePatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantScheduleRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantScheduleResponse;

import java.util.List;

public interface IRestaurantScheduleService {
    RestaurantScheduleResponse createRestaurantSchedule(RestaurantScheduleRequest request);
    RestaurantScheduleResponse updateRestaurantScheduleById(Long id, RestaurantSchedulePatchRequest request);
    RestaurantScheduleResponse getRestaurantScheduleById(Long id);
    List<RestaurantScheduleResponse> getAllRestaurantSchedulesByRestaurant(String restaurantSlug);
    void deleteRestaurantScheduleById(Long id);
}
