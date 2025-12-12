package com.fo_product.user_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.user_service.dtos.requests.*;
import com.fo_product.user_service.dtos.responses.AuthResponse;
import com.fo_product.user_service.dtos.responses.PendingUserResponse;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IAuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Quản lý đăng ký, đăng nhập và xác thực OTP")
public class AuthController {
    IAuthService authService;

    @Operation(summary = "Đăng ký Customer", description = "Bước 1: Tạo tài khoản cho Khách hàng. Hệ thống sẽ lưu tạm và gửi OTP về email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công, chờ xác thực OTP"),
            @ApiResponse(responseCode = "400", description = "Email đã tồn tại hoặc mật khẩu không đúng định dạng")
    })
    @PostMapping("/sign-up/customer")
    APIResponse<PendingUserResponse> signUpCustomer(@Valid @RequestBody UserRequest request) {
        PendingUserResponse response = authService.createPendingUser(request, "CUSTOMER");
        return APIResponse.<PendingUserResponse>builder()
                .result(response)
                .message("Create user success, waiting for verification")
                .build();
    }

    @Operation(summary = "Đăng ký Merchant", description = "Bước 1: Tạo tài khoản cho Chủ nhà hàng. Hệ thống sẽ lưu tạm và gửi OTP về email.")
    @PostMapping("/sign-up/merchant")
    APIResponse<PendingUserResponse> signUpMerchant(@Valid @RequestBody UserRequest request) {
        PendingUserResponse response = authService.createPendingUser(request, "MERCHANT");
        return APIResponse.<PendingUserResponse>builder()
                .result(response)
                .message("Create user success, waiting for verification")
                .build();
    }

    @Operation(summary = "Xác thực OTP & Kích hoạt", description = "Bước 2: Nhập mã OTP nhận được từ email để kích hoạt tài khoản chính thức.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xác thực thành công, tài khoản đã được tạo"),
            @ApiResponse(responseCode = "400", description = "OTP sai hoặc đã hết hạn")
    })
    @PostMapping("/verify-otp")
    APIResponse<UserResponse> verifyAndCreateUSer(@Valid @RequestBody VerifyOtpRequest request) {
        UserResponse result = authService.verifyAndCreateUser(request);

        return APIResponse.<UserResponse>builder()
                .result(result)
                .message("Verify complete, save user successfully")
                .build();
    }

    @Operation(summary = "Gửi lại OTP", description = "Gửi lại mã OTP mới nếu mã cũ bị hết hạn hoặc không nhận được.")
    @PostMapping("/resend-otp")
    APIResponse<?> resendOtp(@Valid @RequestBody EmailRequest request) {
        authService.resendOtp(request);
        return APIResponse.builder()
                .message("New OTP code sent to your email")
                .build();
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập bằng Email và Mật khẩu để lấy Access Token & Refresh Token.")
    @PostMapping("/login")
    APIResponse<AuthResponse> login (@RequestBody AuthenticateRequest request) {
        var response = authService.authentication(request);
        return APIResponse.<AuthResponse>builder()
                .result(response)
                .message("Login successfully")
                .build();
    }

    @Operation(summary = "Làm mới Token (Refresh)", description = "Dùng Refresh Token để lấy lại Access Token mới khi cái cũ hết hạn.")
    @PostMapping("/refresh")
    APIResponse<AuthResponse> refreshToken(@RequestBody TokenRequest request) throws ParseException, JOSEException {
        var response = authService.refreshToken(request);
        return APIResponse.<AuthResponse>builder()
                .result(response)
                .message("Refresh successfully")
                .build();
    }

    @Operation(summary = "Đăng xuất", description = "Vô hiệu hóa Refresh Token hiện tại.")
    @PostMapping("/logout")
    APIResponse<?> logout(@RequestBody TokenRequest request) throws ParseException, JOSEException {
        authService.logout(request);
        return APIResponse.<AuthResponse>builder()
                .message("Log out successfully")
                .build();
    }
}