package com.fo_product.order_service.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Yêu cầu đặt món chi tiết")
public record OrderItemRequest(

        @Schema(description = "ID của sản phẩm", example = "105")
        @NotNull(message = "NOT_NULL")
        Long productId,

        @Schema(description = "Số lượng mua", example = "2")
        @Min(value = 1, message = "QUANTITY_MIN_1")
        int quantity,

        @Schema(description = "Danh sách ID các Option (Topping) đi kèm", example = "[201, 202]")
        List<Long> optionIds
) {
}