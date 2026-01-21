package com.fo_product.order_service.clients;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.clients.factories.PaymentClientFallBackFactory;
import com.fo_product.order_service.configs.FeignClientInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "payment-service",
        configuration = FeignClientInterceptorConfig.class,
        fallbackFactory = PaymentClientFallBackFactory.class
)
public interface PaymentClient {
    @PostMapping("/api/v1/payment/zalopay/query")
    APIResponse<Map<String, Object>> queryPayment(@RequestParam String appTransId);
}
