package com.fo_product.order_service.dtos.responses;

import java.util.List;
import lombok.Builder;

@Builder
public record DashboardStatsResponse(
    List<DailyStatResponse> chartData
) {}
