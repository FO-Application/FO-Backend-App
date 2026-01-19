package com.fo_product.merchant_service.client;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.feigns.UserDTO;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public APIResponse<UserDTO> getUserById(Long id) {
                log.error("Gọi User Service thất bại. Lý do: {}", cause.getMessage());
                throw new MerchantException(MerchantErrorCode.SERVICE_UNAVAILABLE);
            }
        };
    }
}
