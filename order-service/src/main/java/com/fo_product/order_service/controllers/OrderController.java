package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.requests.OrderRequest;
import com.fo_product.order_service.dtos.responses.OrderResponse;
import com.fo_product.order_service.services.interfaces.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Order Controller", description = "Các API liên quan đến quản lý đơn hàng của Khách hàng")
public class OrderController {
    IOrderService orderService;

    @Operation(
            summary = "Tạo đơn hàng mới (Checkout)",
            description = "API này nhận danh sách món, địa chỉ, phương thức thanh toán để tạo đơn hàng. Sẽ validate kho và giá tiền."
    )
    @PostMapping
    APIResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody OrderRequest orderRequest
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.createOrder(userId, orderRequest);
        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Create order Success")
                .build();
    }

    @Operation(
            summary = "Xem lịch sử đơn hàng của tôi",
            description = "Lấy danh sách đơn hàng của user đang đăng nhập. Hỗ trợ phân trang."
    )
    @GetMapping
    APIResponse<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng đơn trên mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        Page<OrderResponse> result = orderService.getMyOrders(userId, page, size);
        return APIResponse.<Page<OrderResponse>>builder()
                .result(result)
                .message("My orders Success")
                .build();
    }

    @Operation(
            summary = "Xem chi tiết một đơn hàng",
            description = "Lấy thông tin đầy đủ của một đơn hàng theo ID. Chỉ chủ sở hữu mới xem được."
    )
    @GetMapping("/{id}")
    APIResponse<OrderResponse> getOrderById(
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(description = "ID của đơn hàng muốn xem", example = "1001")
            @PathVariable("id") Long orderId
    ){
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.getOrderById(userId, orderId);
        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Get Order Success")
                .build();
    }

    @Operation(
            summary = "Hủy đơn hàng",
            description = "Cho phép khách hàng tự hủy đơn khi trạng thái là CREATED. Nếu quán đã nhận đơn thì không thể hủy."
    )
    @PatchMapping("/{id}/cancel")
    APIResponse<OrderResponse> cancelOrder(
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(description = "ID của đơn hàng muốn hủy", example = "1001")
            @PathVariable("id") Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());

        OrderResponse result = orderService.cancelOrder(userId, orderId);
        return APIResponse.<OrderResponse>builder()
                .result(result)
                .message("Cancel order success")
                .build();
    }
}