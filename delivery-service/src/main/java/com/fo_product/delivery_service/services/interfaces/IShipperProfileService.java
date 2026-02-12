package com.fo_product.delivery_service.services.interfaces;

import com.fo_product.delivery_service.dtos.requests.ShipperRegistrationRequest;
import com.fo_product.delivery_service.dtos.responses.ShipperProfileResponse;

public interface IShipperProfileService {
    ShipperProfileResponse registerShipper(Long userId, ShipperRegistrationRequest request);
    ShipperProfileResponse getShipperProfile(Long userId);
    void updateOnlineStatus(Long userId, boolean isOnline);
}
