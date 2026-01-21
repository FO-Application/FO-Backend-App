package com.fo_product.delivery_service.clients.factories;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.clients.OrderClient;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import com.fo_product.delivery_service.exceptions.DeliveryException;
import com.fo_product.delivery_service.exceptions.OrderException;
import com.fo_product.delivery_service.exceptions.UserException;
import com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode;
import com.fo_product.delivery_service.exceptions.code.OrderErrorCode;
import com.fo_product.delivery_service.exceptions.code.UserErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderClientFallBackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        log.error("Gọi Order Service thất bại. Lý do: {}", cause.getMessage());

        return new OrderClient() {
            @Override
            public APIResponse<OrderDTO> getOrderInternal(Long orderId) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);

                    if (status == 404) {
                        throw new OrderException(OrderErrorCode.ORDER_NOT_EXIST);
                    }
                }
                throw new DeliveryException(DeliveryErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public APIResponse<Void> markAsDelivering(Long orderId) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);

                    if (status == 404) {
                        throw new OrderException(OrderErrorCode.ORDER_NOT_EXIST);
                    } else if (status == 400) {
                        throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
                    }
                }
                throw new DeliveryException(DeliveryErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public APIResponse<Void> markAsCompleted(Long orderId) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);

                    if (status == 404) {
                        throw new OrderException(OrderErrorCode.ORDER_NOT_EXIST);
                    } else if (status == 400) {
                        throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
                    }
                }
                throw new DeliveryException(DeliveryErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }

    private void handleSecurityException(int status) {
        log.error("Lỗi cấu hình Security giữa các Service!");
        if (status == 403) {
            throw new UserException(UserErrorCode.ACCESS_DENIED);
        } else if (status == 401) {
            throw new UserException(UserErrorCode.UNAUTHENTICATED);
        }
    }
}
