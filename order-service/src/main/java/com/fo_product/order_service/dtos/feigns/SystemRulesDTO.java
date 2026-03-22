package com.fo_product.order_service.dtos.feigns;

public record SystemRulesDTO(
    double platformFeePercentage,
    double driverFeePercentage,
    double baseDeliveryFee,
    double perKmFee,
    boolean autoApproveRestaurant
) {}
