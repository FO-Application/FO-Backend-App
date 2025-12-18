package com.fo_product.merchant_service.dtos.requests.option_item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record OptionItemPatchRequest(
        @Schema(description = "Tên mới của tùy chọn (Để null nếu không đổi)", example = "Trân châu đường đen")
        String name,

        @Schema(description = "Giá tiền cộng thêm mới (VND) (Để null nếu không đổi)", example = "7000")
        BigDecimal priceAdjustment,

        @Schema(description = "Trạng thái hiển thị mới (true: Còn hàng, false: Hết hàng). Để null nếu không đổi", example = "true")
        Boolean isAvailable
) implements Serializable {
}