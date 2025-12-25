package com.fo_product.order_service.clients;

import com.fo_product.order_service.configs.FeignClientInterceptorConfig;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignClientInterceptorConfig.class)
public interface UserClient {
    @GetMapping("/api/v1/user/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long id);
}
