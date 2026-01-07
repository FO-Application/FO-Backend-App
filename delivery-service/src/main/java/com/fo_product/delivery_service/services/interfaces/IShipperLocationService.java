package com.fo_product.delivery_service.services.interfaces;

import java.util.List;

public interface IShipperLocationService {
    void updateLocation(Long shipperId, double latitude, double longitude);
    List<Long> findNearbyShippers(double merchantLat, double merchantLng, double radiusKm);
    void removeShipper(Long shipperId);
}
