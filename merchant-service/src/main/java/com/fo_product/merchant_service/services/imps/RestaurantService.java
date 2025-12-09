package com.fo_product.merchant_service.services.imps;

import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.services.interfaces.IRestaurantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestaurantService implements IRestaurantService {
    RestaurantRepository restaurantRepository;


}
