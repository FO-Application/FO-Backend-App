package com.fo_product.merchant_service.services.interfaces.restaurant;

import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantPatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IRestaurantService {
    RestaurantResponse createRestaurant(RestaurantRequest request,  MultipartFile image);
    RestaurantResponse updateRestaurantById(Long id, RestaurantPatchRequest request, MultipartFile image);
    RestaurantResponse getRestaurantById(Long id);
    Page<RestaurantResponse> getAllRestaurants(int page, int size);
    Page<RestaurantResponse> getAllRestaurantsByCuisine(int page, int size, String cuisineSlug);
    void deleteRestaurantById(Long id);
}
