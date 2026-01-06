package com.fo_product.delivery_service.helpers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.clients.OrderClient;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetClientDTO {
    OrderClient orderClient;

    public OrderDTO getOrderDTO(Long orderId) {
        APIResponse<OrderDTO> orderResponse = orderClient.getOrderInternal(orderId);

        if (orderResponse == null) {
            return null;
        }

        return orderResponse.getResult();
    }
}
