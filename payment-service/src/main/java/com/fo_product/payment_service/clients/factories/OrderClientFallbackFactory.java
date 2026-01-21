package com.fo_product.payment_service.clients.factories;

import com.fo_product.payment_service.clients.OrderClient;
import com.fo_product.payment_service.exceptions.OrderException;
import com.fo_product.payment_service.exceptions.PaymentException;
import com.fo_product.payment_service.exceptions.UserException;
import com.fo_product.payment_service.exceptions.codes.OrderErrorCode;
import com.fo_product.payment_service.exceptions.codes.PaymentErrorCode;
import com.fo_product.payment_service.exceptions.codes.UserErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        log.error("Gọi Order Service thất bại. Lý do: {}", cause.getMessage());

        return new OrderClient() {
            @Override
            public void updateAppTransId(Long orderId, String appTransId) {
                if (cause instanceof FeignException fe) {
                    int status1 = fe.status();

                    handleSecurityException(status1);

                    if (status1 == 404) {
                        throw new OrderException(OrderErrorCode.ORDER_NOT_EXIST);
                    }
                }

                throw new PaymentException(PaymentErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public void updateOrderStatus(String appTransId, String status) {
                if (cause instanceof FeignException fe) {
                    int status2 = fe.status();

                    handleSecurityException(status2);

                    if (status2 == 404) {
                        throw new OrderException(OrderErrorCode.ORDER_NOT_FOUND_WITH_APP_TRANS_ID);
                    } else if (status2 == 400) {
                        throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
                    }
                }

                throw new PaymentException(PaymentErrorCode.SERVICE_UNAVAILABLE);
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
