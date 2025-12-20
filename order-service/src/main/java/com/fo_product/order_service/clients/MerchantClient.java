package com.fo_product.order_service.clients;

import com.fo_product.order_service.configs.FeignClientInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "merchant-service", configuration = FeignClientInterceptorConfig.class)
public interface MerchantClient {
    //Viết các giao thức Http lấy dữ liệu vào đây
}
