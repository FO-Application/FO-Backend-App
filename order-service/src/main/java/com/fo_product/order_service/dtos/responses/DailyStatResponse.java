package com.fo_product.order_service.dtos.responses;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DailyStatResponse(
    String name,
    long orders,
    BigDecimal revenue
) {}
