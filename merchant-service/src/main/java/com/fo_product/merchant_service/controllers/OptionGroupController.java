package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_group.OptionGroupRequest;
import com.fo_product.merchant_service.dtos.responses.OptionGroupResponse;
import com.fo_product.merchant_service.services.interfaces.addon.IOptionGroupService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/option-group")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@Tag(name = "Option Group (Topping) Management", description = "Quản lý nhóm tùy chọn cho món ăn (Ví dụ: Size, Mức đường, Topping...)")
public class OptionGroupController {
    IOptionGroupService optionGroupService;

    @Operation(summary = "Tạo nhóm tùy chọn", description = "Tạo một nhóm topping mới gắn với sản phẩm (Ví dụ: Nhóm 'Chọn Size').")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu (Min > Max, không tìm thấy Product...)")
    })
    @PostMapping
    APIResponse<OptionGroupResponse> createOptionGroup(
            @RequestBody @Valid OptionGroupRequest request
    ) {
        OptionGroupResponse result = optionGroupService.createOptionGroup(request);
        return APIResponse.<OptionGroupResponse>builder()
                .result(result)
                .message("Option Group Created")
                .build();
    }

    @Operation(summary = "Cập nhật nhóm tùy chọn", description = "Sửa tên, quy tắc chọn (bắt buộc/không bắt buộc) của nhóm.")
    @PutMapping("/{id}")
    APIResponse<OptionGroupResponse> updateOptionGroup(
            @Parameter(description = "ID nhóm tùy chọn", example = "50")
            @PathVariable("id") Long id,
            @RequestBody OptionGroupPatchRequest request
    ) {
        OptionGroupResponse result = optionGroupService.updateOptionGroup(id, request);
        return APIResponse.<OptionGroupResponse>builder()
                .result(result)
                .message("Option Group Updated Successfully") // Đã sửa message cho đúng
                .build();
    }

    @Operation(summary = "Lấy chi tiết nhóm", description = "Xem thông tin chi tiết một nhóm tùy chọn.")
    @GetMapping("/{id}")
    APIResponse<OptionGroupResponse> getOptionGroup(
            @Parameter(description = "ID nhóm tùy chọn", example = "50")
            @PathVariable("id") Long id
    ) {
        OptionGroupResponse result = optionGroupService.getOptionGroup(id);
        return APIResponse.<OptionGroupResponse>builder()
                .result(result)
                .message("Option Group Found")
                .build();
    }

    @Operation(summary = "Lấy danh sách nhóm", description = "Lấy tất cả các nhóm tùy chọn (có phân trang).")
    @GetMapping("/product/{productId}")
    APIResponse<List<OptionGroupResponse>> getAllOptionGroups(
            @Parameter(description = "ID của món ăn", example = "1")
            @PathVariable("productId") Long productId
    ) {
        List<OptionGroupResponse> result = optionGroupService.getOptionGroupsByProduct(productId);
        return APIResponse.<List<OptionGroupResponse>>builder()
                .result(result)
                .message("All Option Groups Found")
                .build();
    }

    @Operation(summary = "Xóa nhóm tùy chọn", description = "Xóa nhóm topping khỏi hệ thống.")
    @DeleteMapping("/{id}")
    APIResponse<?> deleteOptionGroup(
            @Parameter(description = "ID nhóm cần xóa", example = "50")
            @PathVariable("id") Long id
    ) {
        optionGroupService.deleteOptionGroup(id);
        return APIResponse.builder()
                .message("Option Group Deleted Successfully")
                .build();
    }
}