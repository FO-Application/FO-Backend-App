package com.fo_product.delivery_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.services.interfaces.IDeliveryService;
import com.fo_product.delivery_service.services.interfaces.IShipperLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Shipper Delivery Controller", description = "API dành cho Shipper: Cập nhật vị trí, Nhận đơn & Xử lý giao hàng")
public class ShipperController {
    IShipperLocationService shipperLocationService;
    IDeliveryService deliveryService;

    @Operation(
            summary = "Cập nhật vị trí Shipper (Real-time)",
            description = "API này được Mobile App gọi liên tục (VD: 10s/lần) khi Shipper đang bật chế độ 'Trực tuyến'. Hệ thống sẽ lưu tọa độ vào Redis GEO để tìm đơn.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/location")
    public APIResponse<Void> updateLocation(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Vĩ độ (Latitude)", example = "21.028511")
            @RequestParam double lat,

            @Parameter(description = "Kinh độ (Longitude)", example = "105.854444")
            @RequestParam double lon
    ) {
        Long shipperId = Long.valueOf(jwt.getClaim("user-id").toString());
        shipperLocationService.updateLocation(shipperId, lat, lon);
        return APIResponse.<Void>builder()
                .message("Location updated")
                .build();
    }

    @Operation(
            summary = "Tắt trạng thái trực tuyến (Offline)",
            description = "Gọi khi Shipper muốn nghỉ ngơi. Hệ thống sẽ xóa vị trí khỏi Redis để không bắn đơn mới cho Shipper này nữa.(Lấy id của shipper qua token)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/offline")
    public APIResponse<Void> goOffline(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long shipperId = Long.valueOf(jwt.getClaim("user-id").toString());
        shipperLocationService.removeShipper(shipperId);
        return APIResponse.<Void>builder()
                .message("Shipper is now offline")
                .build();
    }

    @Operation(
            summary = "Shipper CHẤP NHẬN đơn hàng",
            description = "Shipper bấm nút 'Nhận đơn' trên App. Hệ thống sẽ kiểm tra xem đơn còn hay không, tạo bản ghi Delivery và dừng tìm kiếm tài xế khác.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/accept")
    public APIResponse<Void> acceptOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID của đơn hàng muốn nhận")
            @RequestParam Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        deliveryService.acceptOrder(userId, orderId);
        return APIResponse.<Void>builder()
                .message("Đã nhận đơn thành công! Hãy di chuyển đến quán.")
                .build();
    }

    @Operation(
            summary = "Xác nhận ĐÃ LẤY HÀNG (Picked Up)",
            description = "Gọi khi Shipper đã đến quán và nhận món ăn xong. Trạng thái đơn hàng chuyển sang DELIVERING. Khách hàng sẽ nhận được thông báo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/picked-up")
    public APIResponse<Void> updatePickedUp(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID của đơn hàng đang giao")
            @RequestParam Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        deliveryService.updatePickedUp(userId, orderId);
        return APIResponse.<Void>builder()
                .message("Đã lấy hàng, trạng thái chuyển sang DELIVERING.")
                .build();
    }

    @Operation(
            summary = "Xác nhận GIAO THÀNH CÔNG (Completed)",
            description = "Gọi khi Shipper đã giao món cho khách. Trạng thái đơn hàng chuyển sang COMPLETED. Tiền phí ship sẽ được cộng vào ví Shipper.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/complete")
    public APIResponse<Void> completeOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID của đơn hàng vừa giao xong")
            @RequestParam Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        deliveryService.completeOrder(userId, orderId);
        return APIResponse.<Void>builder()
                .message("Giao hàng thành công! Tiền đã được cộng vào ví.")
                .build();
    }
}