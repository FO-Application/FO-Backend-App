package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.category.CategoryPatchRequest;
import com.fo_product.merchant_service.dtos.requests.category.CategoryRequest;
import com.fo_product.merchant_service.dtos.responses.CategoryResponse;
import com.fo_product.merchant_service.services.interfaces.product.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category Management", description = "Quản lý phân loại thực đơn (Ví dụ: Khai vị, Đồ uống...)")
public class CategoryController {
    ICategoryService categoryService;

    @Operation(summary = "Tạo danh mục mới", description = "Tạo một category mới cho nhà hàng.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    APIResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        CategoryResponse result =  categoryService.createCategory(request);
        return APIResponse.<CategoryResponse>builder()
                .result(result)
                .message("Category created successfully")
                .build();
    }

    @Operation(summary = "Cập nhật danh mục", description = "Sửa tên hoặc thông tin danh mục.")
    @PutMapping("/{categoryId}")
    APIResponse<CategoryResponse> updateCategory(
            @RequestBody CategoryPatchRequest request,
            @Parameter(description = "ID của danh mục", example = "10")
            @PathVariable("categoryId") Long id
    ) {
        CategoryResponse result = categoryService.updateCategory(id, request);
        return APIResponse.<CategoryResponse>builder()
                .result(result)
                .message("Category updated successfully")
                .build();
    }

    @Operation(summary = "Lấy chi tiết danh mục", description = "Xem thông tin một danh mục cụ thể.")
    @GetMapping("/{categoryId}")
    APIResponse<CategoryResponse> getCategory(
            @Parameter(description = "ID của danh mục", example = "10")
            @PathVariable("categoryId") Long id
    ) {
        CategoryResponse result = categoryService.getCategoryByRestaurant(id);
        return APIResponse.<CategoryResponse>builder()
                .result(result)
                .message("Category get successfully")
                .build();
    }

    @Operation(summary = "Lấy danh sách danh mục", description = "Lấy tất cả danh mục theo nhà hàng")
    @GetMapping("/restaurant/{slug}")
    APIResponse<List<CategoryResponse>> getAllCategories(
            @Parameter(description = "Slug của nhà hàng", example = "pho-anh-hai")
            @PathVariable("slug") String restaurantSlug
    ) {
        List<CategoryResponse> result = categoryService.getAllCategories(restaurantSlug);
        return APIResponse.<List<CategoryResponse>>builder()
                .result(result)
                .message("Category gets successfully")
                .build();
    }

    @Operation(summary = "Xóa danh mục", description = "Xóa danh mục khỏi hệ thống.")
    @DeleteMapping("/{categoryId}")
    APIResponse<?> deleteCategory(
            @Parameter(description = "ID của danh mục cần xóa", example = "10")
            @PathVariable("categoryId") Long id
    ) {
        categoryService.deleteCategoryById(id);
        return APIResponse.builder()
                .message("Category deleted successfully")
                .build();
    }
}