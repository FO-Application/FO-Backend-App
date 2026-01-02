package com.fo_product.delivery_service.clients;

import com.fo_product.delivery_service.configs.FeignClientInterceptorConfig;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", configuration = FeignClientInterceptorConfig.class)
public interface OrderClient {
    @GetMapping("/api/v1/shipping/order/{id}")
    OrderDTO getOrderInternal(@PathVariable("id") Long orderId);
}
