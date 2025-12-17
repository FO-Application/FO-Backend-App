package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantPatchRequest;
import com.fo_product.merchant_service.dtos.requests.restaurant.RestaurantRequest;
import com.fo_product.merchant_service.dtos.responses.RestaurantResponse;
import com.fo_product.merchant_service.services.interfaces.restaurant.IRestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Restaurant Management", description = "Quản lý thông tin nhà hàng (kèm upload ảnh)")
public class RestaurantController {
    IRestaurantService restaurantService;

    @Operation(summary = "Tạo nhà hàng mới", description = "Tạo nhà hàng cần upload ảnh đại diện và thông tin JSON.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi validate dữ liệu hoặc file ảnh")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<RestaurantResponse> createRestaurant(
            @Parameter(description = "Thông tin nhà hàng (JSON)", schema = @Schema(implementation = RestaurantRequest.class))
            @RequestPart("data") @Valid  RestaurantRequest restaurantRequest,

            @Parameter(description = "Ảnh đại diện nhà hàng (.jpg, .png)")
            @RequestPart("image") MultipartFile image
    ) {
        RestaurantResponse result = restaurantService.createRestaurant(restaurantRequest, image);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Create restaurant successful")
                .build();
    }

    @Operation(summary = "Cập nhật nhà hàng", description = "Cập nhật thông tin nhà hàng và ảnh đại diện.")
    @PutMapping(value = "/{restaurantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<RestaurantResponse> updateRestaurant(
            @Parameter(description = "ID nhà hàng", example = "1")
            @PathVariable("restaurantId") Long id,

            @Parameter(description = "Dữ liệu cập nhật (JSON)", schema = @Schema(implementation = RestaurantPatchRequest.class))
            @RequestPart("data") RestaurantPatchRequest restaurantRequest,

            @Parameter(description = "File ảnh mới")
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        RestaurantResponse result = restaurantService.updateRestaurantById(id, restaurantRequest, image);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Update restaurant successful")
                .build();
    }

    @Operation(summary = "Lấy chi tiết nhà hàng", description = "Xem thông tin chi tiết của một nhà hàng theo ID.")
    @GetMapping("/{restaurantId}")
    APIResponse<RestaurantResponse> getRestaurant(
            @Parameter(description = "ID nhà hàng", example = "1")
            @PathVariable("restaurantId") Long id
    ) {
        RestaurantResponse result = restaurantService.getRestaurantById(id);
        return APIResponse.<RestaurantResponse>builder()
                .result(result)
                .message("Get restaurant by id")
                .build();
    }

    @Operation(summary = "Lấy danh sách nhà hàng (Phân trang)", description = "Lấy tất cả nhà hàng với phân trang.")
    @GetMapping
    APIResponse<Page<RestaurantResponse>> getAllRestaurants(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng phần tử mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<RestaurantResponse> result = restaurantService.getAllRestaurants(page, size);
        return APIResponse.<Page<RestaurantResponse>>builder()
                .result(result)
                .message("Get all restaurants")
                .build();
    }

    @Operation(summary = "Lấy danh sách nhà hàng theo loại hình ẩm thực (Phân trang)", description = "Lấy tất cả nhà hàng với phân trang")
    @GetMapping("/cuisine/{slug}")
    APIResponse<Page<RestaurantResponse>> getAllRestaurantsByCuisine(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng phần tử mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Tên slug của loại hình ẩm thực", example = "bun-bo-hue")
            @PathVariable("slug") String slug
    ) {
        Page<RestaurantResponse> result = restaurantService.getAllRestaurantsByCuisine(page, size, slug);
        return APIResponse.<Page<RestaurantResponse>>builder()
                .result(result)
                .message("Get all restaurants")
                .build();
    }

    @Operation(summary = "Xóa nhà hàng", description = "Xóa nhà hàng khỏi hệ thống.")
    @DeleteMapping("/{restaurantId}")
    APIResponse<?> deleteRestaurant(
            @Parameter(description = "ID nhà hàng cần xóa", example = "1")
            @PathVariable("restaurantId") Long id
    ) {
        restaurantService.deleteRestaurantById(id);
        return APIResponse.builder()
                .message("Delete restaurant by id")
                .build();
    }
}