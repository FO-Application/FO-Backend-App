package com.fo_product.delivery_service.clients.factories;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.clients.MerchantClient;
import com.fo_product.delivery_service.dtos.feigns.SystemRulesDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MerchantClientFallbackFactory implements FallbackFactory<MerchantClient> {

    @Override
    public MerchantClient create(Throwable cause) {
        log.error("Gọi Merchant Service thất bại (Delivery Service). Lý do: {}", cause.getMessage());

        return new MerchantClient() {
            @Override
            public APIResponse<SystemRulesDTO> getSystemRules() {
                return APIResponse.<SystemRulesDTO>builder()
                        .result(new SystemRulesDTO(20.0, 20.0, 15000.0, 5000.0, false))
                        .message("Fallback used")
                        .build();
            }
        };
    }
}
