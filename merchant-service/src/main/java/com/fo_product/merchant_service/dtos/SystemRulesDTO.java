package com.fo_product.merchant_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemRulesDTO {
    private double platformFeePercentage;
    private double driverFeePercentage;
    private double baseDeliveryFee;
    private double perKmFee;
    private boolean autoApproveRestaurant;
}
