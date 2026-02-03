package com.fo_product.delivery_service.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipperRegistrationRequest {
    private String vehicleNumber;
    private String vehicleType;
}
