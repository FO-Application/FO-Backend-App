package com.fo_product.delivery_service.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipperProfileResponse {
    private Long id;
    private Long userId;
    private String vehicleNumber;
    private String vehicleType;
    private boolean isOnline;
    private boolean isAvailable;
}
