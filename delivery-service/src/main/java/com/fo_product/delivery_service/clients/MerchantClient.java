package com.fo_product.delivery_service.clients;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.clients.factories.MerchantClientFallbackFactory;
import com.fo_product.delivery_service.configs.FeignClientInterceptorConfig;
import com.fo_product.delivery_service.dtos.feigns.SystemRulesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "merchant-service",
        configuration = FeignClientInterceptorConfig.class,
        fallbackFactory =  MerchantClientFallbackFactory.class
)
public interface MerchantClient {
    @GetMapping("/api/v1/system/rules")
    APIResponse<SystemRulesDTO> getSystemRules();
}
