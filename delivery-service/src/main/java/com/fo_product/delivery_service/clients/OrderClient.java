package com.fo_product.delivery_service.clients;

import com.fo_product.delivery_service.configs.FeignClientInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service", configuration = FeignClientInterceptorConfig.class)
public interface OrderClient {

}
