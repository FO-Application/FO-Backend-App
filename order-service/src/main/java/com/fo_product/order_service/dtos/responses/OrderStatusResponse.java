package com.fo_product.order_service.dtos.responses;

public record OrderStatusResponse(
        String status,
        String message
) {
}
