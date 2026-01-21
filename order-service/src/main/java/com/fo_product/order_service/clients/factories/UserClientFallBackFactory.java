package com.fo_product.order_service.clients.factories;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.clients.UserClient;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.UserException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.exceptions.codes.UserErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallBackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        log.error("Gọi User Service thất bại. Lý do: {}", cause.getMessage());

        return new UserClient() {
            @Override
            public APIResponse<UserDTO> getUserById(Long id) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);

                    if (status == 404) {
                        throw new UserException(UserErrorCode.USER_NOT_EXIST);
                    }
                }

                throw new OrderException(OrderErrorCode.SERVICE_UNAVAILABLE);
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
