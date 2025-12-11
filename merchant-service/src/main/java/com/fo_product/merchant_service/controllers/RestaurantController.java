package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantPatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.services.interfaces.IRestaurantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantController {
    IRestaurantService restaurantService;

    @PostMapping
    APIResponse<RestaurantResponse> createRestaurant(
            @RequestPart("data") RestaurantRequest restaurantRequest,
            @RequestPart("image") MultipartFile image
    ) {
        RestaurantResponse result = restaurantService.createRestaurant(restaurantRequest, image);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Create restaurant successful")
                .build();
    }

    @PutMapping("/{restaurantId}")
    APIResponse<RestaurantResponse> updateRestaurant(
            @PathVariable("restaurantId") Long id,
            @RequestPart("data") RestaurantPatchRequest restaurantRequest,
            @RequestPart("image") MultipartFile image
    ) {
        RestaurantResponse result = restaurantService.updateRestaurantById(id, restaurantRequest, image);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Update restaurant successful")
                .build();
    }

    @GetMapping("/{restaurantId}")
    APIResponse<RestaurantResponse> getRestaurant(@PathVariable("restaurantId") Long id) {
        RestaurantResponse result = restaurantService.getRestaurantById(id);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Get restaurant by id")
                .build();
    }

    @GetMapping
    APIResponse<Page<RestaurantResponse>> getAllRestaurants(
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<RestaurantResponse> result = restaurantService.getAllRestaurants(page, size);
        return APIResponse.<Page<RestaurantResponse>>builder()
                .result(result)
                .message("Get all restaurants")
                .build();
    }

    @DeleteMapping("/{restaurantId}")
    APIResponse<?> deleteRestaurant(@PathVariable("restaurantId") Long id) {
        restaurantService.deleteRestaurantById(id);
        return APIResponse.builder()
                .message("Delete restaurant by id")
                .build();
    }
}
