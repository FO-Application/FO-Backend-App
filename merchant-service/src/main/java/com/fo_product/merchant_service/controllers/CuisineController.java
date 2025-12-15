package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisinePatchRequest;
import com.fo_product.merchant_service.dtos.requests.cuisine.CuisineRequest;
import com.fo_product.merchant_service.dtos.responses.CuisineResponse;
import com.fo_product.merchant_service.services.interfaces.restaurant.ICuisineService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuisine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Cuisine Management", description = "Quản lý loại ẩm thực (kèm upload ảnh)")
public class CuisineController {
    ICuisineService cuisineService;

    @Operation(summary = "Tạo mới Cuisine", description = "Tạo loại ẩm thực mới kèm theo ảnh minh họa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc file ảnh lỗi")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<CuisineResponse> createCuisine(
            @Parameter(description = "Thông tin Cuisine (JSON)", schema = @Schema(implementation = CuisineRequest.class))
            @RequestPart("data") @Valid CuisineRequest request,

            @Parameter(description = "File ảnh đại diện (.jpg, .png)")
            @RequestPart("image") MultipartFile image
    ) {
        CuisineResponse result = cuisineService.createCuisine(request, image);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Create cuisine")
                .build();
    }

    @Operation(summary = "Cập nhật Cuisine", description = "Cập nhật thông tin hoặc thay đổi ảnh (nếu có gửi file ảnh mới).")
    @PutMapping(value = "/{cuisineId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<CuisineResponse> updateCuisine(
            @Parameter(description = "ID của Cuisine", example = "1")
            @PathVariable("cuisineId") Long id,

            @Parameter(description = "Dữ liệu cần sửa (JSON)")
            @RequestPart("data") CuisinePatchRequest request,

            @Parameter(description = "File ảnh mới (Bỏ trống nếu không muốn đổi ảnh)")
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        CuisineResponse result = cuisineService.updateCuisineById(id, request, image);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Update cuisine")
                .build();
    }

    @Operation(summary = "Lấy chi tiết Cuisine", description = "Lấy thông tin một loại ẩm thực theo ID.")
    @GetMapping("/{cuisineId}")
    APIResponse<CuisineResponse> getCuisineById(
            @Parameter(description = "ID Cuisine", example = "1")
            @PathVariable("cuisineId") Long id
    ) {
        CuisineResponse result = cuisineService.getCuisineById(id);
        return APIResponse.<CuisineResponse>builder()
                .result(result)
                .message("Get cuisine by id")
                .build();
    }

    @Operation(summary = "Lấy tất cả Cuisine", description = "Trả về danh sách toàn bộ loại ẩm thực.")
    @GetMapping
    APIResponse<List<CuisineResponse>> getAllCuisines() {
        List<CuisineResponse> result = cuisineService.getAllCuisines();
        return APIResponse.<List<CuisineResponse>>builder()
                .result(result)
                .message("Get all cuisines")
                .build();
    }

    @Operation(summary = "Xóa Cuisine", description = "Xóa loại ẩm thực khỏi hệ thống.")
    @DeleteMapping("/{cuisineId}")
    APIResponse<?> deleteCuisine(
            @Parameter(description = "ID Cuisine cần xóa", example = "1")
            @PathVariable("cuisineId") Long id
    ) {
        cuisineService.deleteCuisineById(id);
        return APIResponse.builder()
                .message("Delete cuisine")
                .build();
    }
}