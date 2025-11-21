package com.fo_product.user_service.controllers;

import com.fo_product.user_service.resources.APIResponse;
import com.fo_product.user_service.resources.requests.UserPatchRequest;
import com.fo_product.user_service.resources.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    IUserService userService;

    @PutMapping("/{userId}")
    APIResponse<UserResponse> update(
            @PathVariable("userId") Long userId,
            @RequestBody UserPatchRequest request
    ) {
        UserResponse response = userService.updateUserById(userId, request);
        return APIResponse.<UserResponse>builder()
                .result(response)
                .message("Update user " + userId + " successfully")
                .build();
    }

    @GetMapping("/{userId}")
    APIResponse<UserResponse> getById(@PathVariable("userId") Long userId) {
        UserResponse response = userService.getUserById(userId);
        return APIResponse.<UserResponse>builder()
                .result(response)
                .message("Get user by id successfully")
                .build();
    }

    @GetMapping
    APIResponse<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<UserResponse> responses = userService.getAllUsers(page, size);
        return APIResponse.<Page<UserResponse>>builder()
                .result(responses)
                .message("Get all user successfully")
                .build();
    }

    @DeleteMapping("/{userId}")
    APIResponse<?> delete(@PathVariable("userId") Long userId) {
        userService.deleteUserById(userId);

        return APIResponse.builder()
                .message("Delete user " + userId + " successfully")
                .build();
    }
}
