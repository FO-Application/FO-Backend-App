package com.fo_product.delivery_service.services.imps;

import com.fo_product.delivery_service.dtos.requests.ShipperRegistrationRequest;
import com.fo_product.delivery_service.dtos.responses.ShipperProfileResponse;
import com.fo_product.delivery_service.exceptions.DeliveryException;
import com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode;
import com.fo_product.delivery_service.models.entities.Shipper;
import com.fo_product.delivery_service.models.repositories.ShipperRepository;
import com.fo_product.delivery_service.services.interfaces.IShipperProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipperProfileService implements IShipperProfileService {
    ShipperRepository shipperRepository;

    @Override
    @Transactional
    public ShipperProfileResponse registerShipper(Long userId, ShipperRegistrationRequest request) {
        // Kiểm tra xem đã tồn tại chưa
        if (shipperRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Shipper profile already exists");
        }

        Shipper newShipper = Shipper.builder()
                .userId(userId)
                .vehicleNumber(request.getVehicleNumber())
                .vehicleType(request.getVehicleType())
                .isOnline(false)
                .isAvailable(true)
                .build();

        Shipper savedShipper = shipperRepository.save(newShipper);
        return mapToResponse(savedShipper);
    }

    @Override
    public ShipperProfileResponse getShipperProfile(Long userId) {
        Shipper shipper = shipperRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Shipper profile not found"));
        return mapToResponse(shipper);
    }

    private ShipperProfileResponse mapToResponse(Shipper shipper) {
        return ShipperProfileResponse.builder()
                .id(shipper.getId())
                .userId(shipper.getUserId())
                .vehicleNumber(shipper.getVehicleNumber())
                .vehicleType(shipper.getVehicleType())
                .isOnline(shipper.isOnline())
                .isAvailable(shipper.isAvailable())
                .build();
    }

    @Override
    @Transactional
    public void updateOnlineStatus(Long userId, boolean isOnline) {
        shipperRepository.findByUserId(userId).ifPresent(shipper -> {
            shipper.setOnline(isOnline);
            shipperRepository.save(shipper);
        });
    }
}
