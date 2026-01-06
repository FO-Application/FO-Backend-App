package com.fo_product.order_service.helpers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.clients.MerchantClient;
import com.fo_product.order_service.clients.UserClient;
import com.fo_product.order_service.dtos.feigns.ProductDTO;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetClientDTO {
    UserClient userClient;
    MerchantClient merchantClient;

    public UserDTO getUserDTO(Long userId) {
        APIResponse<UserDTO> userResponse = userClient.getUserById(userId);

        if (userResponse == null) {
            return null;
        }

        return userResponse.getResult();
    }

    public RestaurantDTO getRestaurantDTO(Long merchantId) {
        APIResponse<RestaurantDTO> restaurantResponse = merchantClient.getRestaurant(merchantId);

        if (restaurantResponse == null) {
            return null;
        }

        return restaurantResponse.getResult();
    }

    public List<ProductDTO> getProductDTOs(List<Long> productIds) {
        APIResponse<List<ProductDTO>> productsResponse = merchantClient.getAllProductsByIds(productIds);

        if (productsResponse == null) {
            return null;
        }

        return productsResponse.getResult();
    }
}
