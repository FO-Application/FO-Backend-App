package com.fo_product.order_service.mappers;

import com.fo_product.order_service.dtos.responses.OrderItemOptionResponse;
import com.fo_product.order_service.dtos.responses.OrderItemResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.dtos.responses.ReviewResponse;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.clients.UserClient;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import com.fo_product.common_lib.dtos.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderMapper {
    UserClient userClient;

    public OrderResponse response(Order order) {
        String driverName = null;
        if (order.getShipperId() != null) {
            try {
                APIResponse<UserDTO> apiResponse = userClient.getUserById(order.getShipperId());
                if (apiResponse != null && apiResponse.getResult() != null) {
                    UserDTO user = apiResponse.getResult();
                    driverName = (user.lastName() != null ? user.lastName() + " " : "") + 
                                 (user.firstName() != null ? user.firstName() : "");
                    driverName = driverName.trim();
                }
            } catch (Exception e) {
                driverName = "Tài xế (ID: " + order.getShipperId() + ")";
            }
        }

        return OrderResponse.builder()
                .id(order.getId())
                .driverName(driverName)
                .userId(order.getUserId())
                .merchantId(order.getMerchantId())
                .merchantName(order.getMerchantName())
                .merchantLogo(order.getMerchantLogo())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .deliveryAddress(order.getDeliveryAddress())
                .subTotal(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .grandTotal(order.getGrandTotal())
                .descriptionOrder(order.getDescriptionOrder())
                .orderStatus(order.getOrderStatus().name())
                .paymentMethod(order.getPaymentMethod().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem ->
                                OrderItemResponse.builder()
                                        .id(orderItem.getId())
                                        .productId(orderItem.getProductId())
                                        .productName(orderItem.getProductName())
                                        .productImage(orderItem.getProductImage())
                                        .unitPrice(orderItem.getUnitPrice())
                                        .quantity(orderItem.getQuantity())
                                        .totalPrice(orderItem.getTotalPrice())
                                        .options(orderItem.getOrderItemOptions().stream()
                                                .map(orderItemOption ->
                                                                OrderItemOptionResponse.builder()
                                                                        .optionGroupName(orderItemOption.getOptionGroupName())
                                                                        .optionName(orderItemOption.getOptionName())
                                                                        .priceAdjustment(orderItemOption.getPriceAdjustment())
                                                                        .build()
                                                ).toList()
                                        )
                                        .build()
                        ).toList()
                )
                .review(order.getReview() != null ?
                        ReviewResponse.builder()
                                .id(order.getReview().getId())
                                .rating(order.getReview().getRating())
                                .comment(order.getReview().getComment())
                                .createdAt(order.getReview().getCreatedAt())
                                .build()
                        : null
                )
                .build();
    }
}
