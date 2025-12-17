package com.fo_product.merchant_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemPatchRequest;
import com.fo_product.merchant_service.dtos.requests.option_item.OptionItemRequest;
import com.fo_product.merchant_service.dtos.responses.OptionItemResponse;
import com.fo_product.merchant_service.services.interfaces.addon.IOptionItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/option-item")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Option Item (Detail) Management", description = "Quản lý chi tiết topping (Ví dụ: Trân châu trắng, Đường 50%...)")
public class OptionItemController {
    IOptionItemService optionItemService;

    @Operation(summary = "Tạo tùy chọn con", description = "Thêm một lựa chọn vào nhóm (Ví dụ: Thêm 'Trân châu' vào nhóm 'Topping').")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu")
    })
    @PostMapping
    APIResponse<OptionItemResponse> createOptionItem(
            @RequestBody @Valid OptionItemRequest request
    ) {
        OptionItemResponse result = optionItemService.createOptionItem(request);
        return APIResponse.<OptionItemResponse>builder()
                .result(result)
                .message("Option Item Created")
                .build();
    }

    @Operation(summary = "Cập nhật tùy chọn", description = "Sửa tên, giá tiền thêm vào.")
    @PutMapping("/{id}")
    APIResponse<OptionItemResponse> updateOptionItem(
            @Parameter(description = "ID tùy chọn", example = "100")
            @PathVariable("id") Long id,
            @RequestBody OptionItemPatchRequest request
    ) {
        OptionItemResponse result = optionItemService.updateOptionItem(id, request);
        return APIResponse.<OptionItemResponse>builder()
                .result(result)
                .message("Option Item Updated")
                .build();
    }

    @Operation(summary = "Lấy chi tiết tùy chọn", description = "Xem thông tin một option cụ thể.")
    @GetMapping("/{id}")
    APIResponse<OptionItemResponse> getOptionItem(
            @PathVariable("id") Long id
    ) {
        OptionItemResponse result = optionItemService.getOptionItem(id);
        return APIResponse.<OptionItemResponse>builder()
                .result(result)
                .message("Option Item Found")
                .build();
    }

    @Operation(summary = "Lấy danh sách tùy chọn theo Nhóm", description = "Lấy tất cả các option trong một nhóm (Ví dụ: Lấy hết các loại size trong nhóm Size).")
    @GetMapping("/group/{groupId}")
    APIResponse<List<OptionItemResponse>> getOptionItemsByGroup(
            @Parameter(description = "ID của nhóm option", example = "1")
            @PathVariable("groupId") Long groupId
    ) {
        List<OptionItemResponse> result = optionItemService.getOptionItemsByGroupId(groupId);
        return APIResponse.<List<OptionItemResponse>>builder()
                .result(result)
                .message("Option Items Found")
                .build();
    }

    @Operation(summary = "Xóa tùy chọn", description = "Xóa option khỏi hệ thống.")
    @DeleteMapping("/{id}")
    APIResponse<?> deleteOptionItem(
            @PathVariable("id") Long id
    ) {
        optionItemService.deleteOptionItem(id);
        return APIResponse.builder()
                .message("Option Item Deleted")
                .build();
    }
}