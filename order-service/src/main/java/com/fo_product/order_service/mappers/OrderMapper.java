package com.fo_product.order_service.mappers;

import com.fo_product.order_service.dtos.responses.OrderItemOptionResponse;
import com.fo_product.order_service.dtos.responses.OrderItemResponse;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.models.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse response(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .merchantId(order.getMerchantId())
                .merchantName(order.getMerchantName())
                .merchantLogo(order.getMerchantLogo())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
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
                .build();
    }
}
