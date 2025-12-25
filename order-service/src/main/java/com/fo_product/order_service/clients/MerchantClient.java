package com.fo_product.order_service.clients;

import com.fo_product.order_service.configs.FeignClientInterceptorConfig;
import com.fo_product.order_service.dtos.feigns.ProductDTO;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "merchant-service", configuration = FeignClientInterceptorConfig.class)
public interface MerchantClient {
    @GetMapping("/api/v1/restaurant/{restaurantId}")
    RestaurantDTO getRestaurant(@PathVariable("restaurantId") Long id);

    @GetMapping("/api/v1/product/products")
    List<ProductDTO> getAllProductsByIds(@RequestParam List<Long> productIds);
}
