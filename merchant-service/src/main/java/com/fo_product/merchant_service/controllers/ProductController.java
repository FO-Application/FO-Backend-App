package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.product.ProductPatchRequest;
import com.fo_product.merchant_service.dtos.requests.product.ProductRequest;
import com.fo_product.merchant_service.dtos.responses.ProductResponse;
import com.fo_product.merchant_service.services.interfaces.product.IProductService;
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
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product Management", description = "Quản lý sản phẩm (Đồ ăn) của nhà hàng (kèm upload ảnh)")
public class ProductController {
    IProductService productService;

    @Operation(summary = "Tạo mới sản phẩm", description = "Thêm một món ăn mới vào thực đơn, kèm theo ảnh.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào hoặc ảnh lỗi")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<ProductResponse> createProduct(
            @Parameter(description = "Thông tin chi tiết sản phẩm (JSON)", schema = @Schema(implementation = ProductRequest.class))
            @Valid @RequestPart("data") ProductRequest request,

            @Parameter(description = "File ảnh minh họa (.jpg, .png)")
            @RequestPart("image") MultipartFile image
    ) {
        ProductResponse result = productService.createProduct(request, image);
        return APIResponse.<ProductResponse>builder()
                .result(result)
                .message("Product successfully created")
                .build();
    }

    @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin món ăn hoặc thay đổi ảnh.")
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<ProductResponse> updateProduct(
            @Parameter(description = "ID sản phẩm", example = "100")
            @PathVariable("productId") Long id,

            @Parameter(description = "Dữ liệu cần sửa (JSON)")
            @RequestPart("data") ProductPatchRequest request,

            @Parameter(description = "File ảnh mới (Để trống nếu không đổi ảnh)")
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductResponse result = productService.updateProduct(id, request, image);
        return APIResponse.<ProductResponse>builder()
                .result(result)
                .message("Product successfully updated")
                .build();
    }

    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Xem thông tin chi tiết một món ăn.")
    @GetMapping("/{productId}")
    APIResponse<ProductResponse> getProductById(
            @Parameter(description = "ID sản phẩm", example = "100")
            @PathVariable("productId") Long id
    ) {
        ProductResponse result = productService.getById(id);
        return APIResponse.<ProductResponse>builder()
                .result(result)
                .message("Product successfully retrieved")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm", description = "Lấy toàn bộ món ăn (có phân trang).")
    @GetMapping
    APIResponse<Page<ProductResponse>> getProducts(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng phần tử mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> result = productService.getAllProducts(page, size);
        return APIResponse.<Page<ProductResponse>>builder()
                .result(result)
                .message("Product successfully retrieved")
                .build();
    }

    @Operation(summary = "Xóa sản phẩm", description = "Xóa món ăn khỏi thực đơn.")
    @DeleteMapping("/{productId}")
    APIResponse<ProductResponse> deleteProductById(
            @Parameter(description = "ID sản phẩm cần xóa", example = "100")
            @PathVariable("productId") Long id
    ) {
        productService.deleteProduct(id);
        return APIResponse.<ProductResponse>builder()
                .message("Product successfully deleted")
                .build();
    }
}