package com.fo_product.payment_service.clients;

import com.fo_product.payment_service.clients.factories.OrderClientFallbackFactory;
import com.fo_product.payment_service.configs.FeignClientInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "order-service",
        configuration = FeignClientInterceptorConfig.class,
        fallbackFactory = OrderClientFallbackFactory.class
)
public interface OrderClient {
    @PostMapping("/api/internal/orders/{orderId}/update-trans-id")
    void updateAppTransId(@PathVariable("orderId") Long orderId, @RequestParam("appTransId") String appTransId);

    @PostMapping("/api/internal/orders/update-status-by-trans-id")
    void updateOrderStatus(@RequestParam("appTransId") String appTransId, @RequestParam("status") String status);
}
