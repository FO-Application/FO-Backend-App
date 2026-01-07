package com.fo_product.delivery_service.helpers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.clients.OrderClient;
import com.fo_product.delivery_service.clients.UserClient;
import com.fo_product.delivery_service.dtos.feigns.OrderDTO;
import com.fo_product.delivery_service.dtos.feigns.UserDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetClientDTO {
    OrderClient orderClient;
    UserClient userClient;

    public OrderDTO getOrderDTO(Long orderId) {
        APIResponse<OrderDTO> orderResponse = orderClient.getOrderInternal(orderId);

        if (orderResponse == null) {
            return null;
        }

        return orderResponse.getResult();
    }

    public UserDTO getUserDTO(Long userId) {
        APIResponse<UserDTO> userResponse = userClient.getUserById(userId);

        if (userResponse == null) {
            return null;
        }

        return userResponse.getResult();
    }
}
