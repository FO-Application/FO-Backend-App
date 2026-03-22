package com.fo_product.order_service.clients.factories;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.clients.MerchantClient;
import com.fo_product.order_service.dtos.feigns.ProductDTO;
import com.fo_product.order_service.dtos.feigns.RestaurantDTO;
import com.fo_product.order_service.dtos.feigns.SystemRulesDTO;
import com.fo_product.order_service.exceptions.MerchantException;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.UserException;
import com.fo_product.order_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.exceptions.codes.UserErrorCode;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MerchantClientFallbackFactory implements FallbackFactory<MerchantClient> {

    @Override
    public MerchantClient create(Throwable cause) {
        log.error("Gọi Merchant Service thất bại. Lý do: {}", cause.getMessage());

        return new MerchantClient() {
            @Override
            public APIResponse<RestaurantDTO> getRestaurant(Long id) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);

                    if (status == 404) {
                        throw new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST);
                    }
                }
                throw new OrderException(OrderErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public APIResponse<List<ProductDTO>> getAllProductsByIds(List<Long> productIds) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();

                    handleSecurityException(status);
                }

                throw new OrderException(OrderErrorCode.SERVICE_UNAVAILABLE);
            }


            @Override
            public APIResponse<Long> countProductsByRestaurant(Long restaurantId) {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();
                    handleSecurityException(status);
                }
                throw new OrderException(OrderErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public APIResponse<SystemRulesDTO> getSystemRules() {
                if (cause instanceof FeignException fe) {
                    int status = fe.status();
                    handleSecurityException(status);
                }
                return APIResponse.<SystemRulesDTO>builder()
                        .result(new SystemRulesDTO(20.0, 20.0, 15000.0, 5000.0, false))
                        .message("Fallback used")
                        .build();
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
