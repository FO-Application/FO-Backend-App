package com.fo_product.order_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.order_service.dtos.requests.ReviewRequest;
import com.fo_product.order_service.dtos.responses.ReviewResponse;
import com.fo_product.order_service.services.interfaces.IReviewService;
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
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Review Controller", description = "API Đánh giá và Bình luận")
public class ReviewController {
    IReviewService reviewService;

    @Operation(summary = "Viết đánh giá cho đơn hàng", description = "User chỉ được đánh giá khi đơn hàng đã COMPLETED và chưa đánh giá trước đó.")
    @PostMapping
    public APIResponse<ReviewResponse> createReview(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long userId = Long.valueOf(jwt.getClaim("user-id").toString());
        ReviewResponse result = reviewService.createReview(userId, request);

        return APIResponse.<ReviewResponse>builder()
                .result(result)
                .message("Review created successfully")
                .build();
    }

    @Operation(summary = "Xem danh sách đánh giá của quán", description = "Dùng cho trang chi tiết quán ăn (Client) hoặc trang quản lý của chủ quán.")
    @GetMapping("/merchant/{merchantId}")
    public APIResponse<Page<ReviewResponse>> getReviewsByMerchant(
            @Parameter(description = "ID của quán ăn", example = "1")
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> result = reviewService.getReviewsByMerchant(merchantId, page, size);
        return APIResponse.<Page<ReviewResponse>>builder()
                .result(result)
                .message("Get merchant reviews success")
                .build();
    }

    @Operation(summary = "Xem đánh giá của một đơn hàng cụ thể", description = "Dùng để hiển thị lại đánh giá cũ trong lịch sử đơn hàng.")
    @GetMapping("/order/{orderId}")
    public APIResponse<ReviewResponse> getReviewByOrder(
            @Parameter(description = "ID đơn hàng", example = "1001")
            @PathVariable Long orderId
    ) {
        ReviewResponse result = reviewService.getReviewByOrder(orderId);
        return APIResponse.<ReviewResponse>builder()
                .result(result)
                .message("Get order review success")
                .build();
    }
}