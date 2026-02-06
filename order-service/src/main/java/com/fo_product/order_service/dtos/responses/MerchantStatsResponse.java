package com.fo_product.order_service.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MerchantStatsResponse {
    private long ordersToday;
    private BigDecimal totalRevenue;
    private double averageRating;
    private long menuItems;
}
