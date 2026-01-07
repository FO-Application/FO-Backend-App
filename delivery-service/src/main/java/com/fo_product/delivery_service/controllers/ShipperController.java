package com.fo_product.delivery_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.services.interfaces.IShipperLocationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery/shippers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipperController {
    IShipperLocationService  shipperLocationService;

    @PostMapping("/location")
    public APIResponse<Void> updateLocation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        Long shipperId = Long.valueOf(jwt.getClaim("user-id").toString());
        shipperLocationService.updateLocation(shipperId, lat, lon);
        return APIResponse.<Void>builder()
                .message("Location updated")
                .build();
    }

    // API: Offline (Xóa vị trí)
    @PostMapping("/offline")
    public APIResponse<Void> goOffline(@RequestParam Long shipperId) {
        shipperLocationService.removeShipper(shipperId);
        return APIResponse.<Void>builder()
                .message("Shipper is now offline")
                .build();
    }
}
