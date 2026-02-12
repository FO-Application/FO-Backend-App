package com.fo_product.delivery_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.delivery_service.dtos.requests.ShipperRegistrationRequest;
import com.fo_product.delivery_service.dtos.responses.ShipperProfileResponse;
import com.fo_product.delivery_service.kafka.events.ShipperFoundEvent;
import com.fo_product.delivery_service.services.interfaces.IDeliveryService;
import com.fo_product.delivery_service.services.interfaces.IOrderMatchingService;
import com.fo_product.delivery_service.services.interfaces.IShipperLocationService;
import com.fo_product.delivery_service.services.interfaces.IShipperProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery/shippers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Shipper Delivery Controller", description = "API dành cho Shipper: Cập nhật vị trí, Nhận đơn & Xử lý giao hàng")
public class ShipperController {
    IShipperLocationService shipperLocationService;
    IDeliveryService deliveryService;
    IShipperProfileService shipperProfileService;
    IOrderMatchingService orderMatchingService;

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
        // Đồng bộ trạng thái online vào DB
        shipperProfileService.updateOnlineStatus(shipperId, true);
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
        // Đồng bộ trạng thái offline vào DB
        shipperProfileService.updateOnlineStatus(shipperId, false);
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
    @Operation(summary = "Đăng ký thông tin Shipper", description = "Dành cho Shipper mới: Đăng ký biển số xe, loại xe", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/register")
    public APIResponse<ShipperProfileResponse> registerShipper(
            @AuthenticationPrincipal Jwt jwt,
            @org.springframework.web.bind.annotation.RequestBody ShipperRegistrationRequest request
    ) {
        // Check Role
        String scope = jwt.getClaim("scope").toString();
        if (!scope.contains("SHIPPER")) {
            throw new com.fo_product.delivery_service.exceptions.DeliveryException(com.fo_product.delivery_service.exceptions.code.DeliveryErrorCode.FORBIDDEN);
        }

        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        return APIResponse.<ShipperProfileResponse>builder()
                .result(shipperProfileService.registerShipper(userId, request))
                .message("Đăng ký thông tin thành công!")
                .build();
    }

    @Operation(summary = "Lấy danh sách đơn hàng đang chờ nhận (Polling)", description = "Dùng để kiểm tra đơn mới nếu lỡ Push Notification. Nên gọi mỗi 5-10s.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/pending-orders")
    public APIResponse<List<ShipperFoundEvent>> getPendingOrders(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        // Shipper ID == User ID (giả định)
        return APIResponse.<java.util.List<com.fo_product.delivery_service.kafka.events.ShipperFoundEvent>>builder()
                .result(orderMatchingService.getPendingOffers(userId))
                .message("Lấy danh sách đơn chờ thành công!")
                .build();
    }

    @Operation(summary = "Lấy thông tin hồ sơ Shipper", description = "Kiểm tra xem User này đã đăng ký làm Shipper chưa", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/profile")
    public APIResponse<ShipperProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        return APIResponse.<ShipperProfileResponse>builder()
                .result(shipperProfileService.getShipperProfile(userId))
                .message("Lấy thông tin thành công!")
                .build();
    }

    @Operation(summary = "Nạp tiền vào ví (Giả lập)", description = "API dùng để nạp tiền vào ví Shipper (Demo)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/wallet/deposit")
    public APIResponse<Void> deposit(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Số tiền muốn nạp")
            @RequestParam java.math.BigDecimal amount
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        deliveryService.deposit(userId, amount);
        return APIResponse.<Void>builder()
                .message("Nạp tiền thành công!")
                .build();
    }

    @Operation(summary = "Xem thông tin Ví & Thống kê", description = "Lấy số dư ví và thu nhập", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/wallet")
    public APIResponse<com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse> getWalletStats(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        return APIResponse.<com.fo_product.delivery_service.dtos.responses.ShipperWalletResponse>builder()
                .result(deliveryService.getWalletStats(userId))
                .message("Lấy thông tin ví thành công!")
                .build();
    }
}