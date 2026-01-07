package com.fo_product.delivery_service.clients;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.configs.FeignClientInterceptorConfig;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service", configuration = FeignClientInterceptorConfig.class)
public interface OrderClient {
    @GetMapping("/api/v1/shipping/order/{id}")
    APIResponse<OrderDTO> getOrderInternal(@PathVariable("id") Long orderId);

    @PutMapping("/api/v1/shipping/order/{id}/delivering")
    APIResponse<Void> markAsDelivering(@PathVariable("id") Long orderId);

    @PutMapping("/api/v1/shipping/order/{id}/completed")
    APIResponse<Void> markAsCompleted(@PathVariable("id") Long orderId);
}
