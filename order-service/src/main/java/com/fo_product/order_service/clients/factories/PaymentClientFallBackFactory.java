package com.fo_product.order_service.clients.factories;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.clients.PaymentClient;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PaymentClientFallBackFactory implements FallbackFactory<PaymentClient> {
    @Override
    public PaymentClient create(Throwable cause) {
        log.error("Gọi Payment Service thất bại. Lý do: {}", cause.getMessage());

        return new PaymentClient() {
            @Override
            public APIResponse<Map<String, Object>> queryPayment(String appTransId) {
                if (cause instanceof FeignException fe) {
                    log.error("Payment Service trả về status: {}", fe.status()); //log check lỗi nếu có xung đột ở service payment
                }

                throw new OrderException(OrderErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}
