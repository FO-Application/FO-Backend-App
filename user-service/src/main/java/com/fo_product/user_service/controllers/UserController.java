package com.fo_product.user_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.user_service.dtos.requests.UserPatchRequest;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Management", description = "Quản lý thông tin người dùng") // Tên nhóm API trên Swagger
public class UserController {
    IUserService userService;

    @Operation(summary = "Cập nhật thông tin user", description = "Cập nhật các trường thông tin cho user theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ (sai định dạng email/sđt)")
    })
    @PutMapping("/{userId}")
    APIResponse<UserResponse> update(
            @Parameter(description = "ID của user cần cập nhật", example = "10")
            @PathVariable("userId") Long userId,
            @RequestBody UserPatchRequest request
    ) {
        UserResponse response = userService.updateUserById(userId, request);
        return APIResponse.<UserResponse>builder()
                .result(response)
                .message("Update user " + userId + " successfully")
                .build();
    }

    @Operation(summary = "Lấy chi tiết user", description = "Lấy thông tin chi tiết của một user cụ thể theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "User không tồn tại")
    })
    @GetMapping("/{userId}")
    APIResponse<UserResponse> getById(
            @Parameter(description = "ID của user", example = "1")
            @PathVariable("userId") Long userId
    ) {
        UserResponse response = userService.getUserById(userId);
        return APIResponse.<UserResponse>builder()
                .result(response)
                .message("Get user by id successfully")
                .build();
    }

    @Operation(summary = "Lấy thông tin bản thân (My Info)", description = "Lấy thông tin của người dùng hiện tại dựa trên Token đăng nhập.")
    @GetMapping("/me")
    APIResponse<UserResponse> getMe() {
        UserResponse response = userService.getMe();
        return APIResponse.<UserResponse>builder()
                .result(response)
                .message("Get me successfully")
                .build();
    }

    @Operation(summary = "Lấy danh sách user (Phân trang)", description = "Lấy danh sách tất cả user có hỗ trợ phân trang.")
    @GetMapping
    APIResponse<Page<UserResponse>> getAll(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng phần tử mỗi trang", example = "10")
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<UserResponse> responses = userService.getAllUsers(page, size);
        return APIResponse.<Page<UserResponse>>builder()
                .result(responses)
                .message("Get all user successfully")
                .build();
    }

    @Operation(summary = "Xóa user", description = "Xóa cứng (hoặc mềm tùy logic) user khỏi hệ thống.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền xóa"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user để xóa")
    })
    @DeleteMapping("/{userId}")
    APIResponse<?> delete(
            @Parameter(description = "ID user cần xóa", example = "99")
            @PathVariable("userId") Long userId
    ) {
        userService.deleteUserById(userId);

        return APIResponse.builder()
                .message("Delete user " + userId + " successfully")
                .build();
    }
}