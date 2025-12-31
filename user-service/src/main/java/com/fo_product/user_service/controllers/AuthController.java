package com.fo_product.user_service.controllers;

import com.fo_product.common_lib.dtos.APIResponse;
import com.fo_product.user_service.dtos.requests.*;
import com.fo_product.user_service.dtos.responses.AuthenticationDTO;
import com.fo_product.user_service.dtos.responses.AuthenticationResponse;
import com.fo_product.user_service.dtos.responses.PendingUserResponse;
import com.fo_product.user_service.dtos.responses.UserResponse;
import com.fo_product.user_service.services.interfaces.IAuthCookieService;
import com.fo_product.user_service.services.interfaces.IAuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Quản lý xác thực. Token được lưu trong **HttpOnly Cookies**. API Login/Refresh trả về Role trong Body.")
public class AuthController {
    IAuthService authService;
    IAuthCookieService authCookieService;

    @Operation(summary = "Đăng ký Customer", description = "Bước 1: Tạo tài khoản cho Khách hàng. Hệ thống sẽ lưu tạm và gửi OTP về email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công, chờ xác thực OTP"),
            @ApiResponse(responseCode = "400", description = "Email đã tồn tại hoặc mật khẩu không đúng định dạng")
    })
    @PostMapping("/sign-up/customer")
    APIResponse<PendingUserResponse> signUpCustomer(@Valid @RequestBody UserRequest request) throws JOSEException, ParseException {
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

    @Operation(summary = "Đăng ký chạy ship", description = "Bước 1: Tạo tài khoản cho shipper. Hệ thống sẽ lưu tạm và gửi OTP về email.")
    @PostMapping("/sign-up/shipper")
    APIResponse<PendingUserResponse> signUpShipper(@Valid @RequestBody UserRequest request) {
        PendingUserResponse response = authService.createPendingUser(request, "SHIPPER");
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

    @Operation(
            summary = "Đăng nhập (Set Cookies + Body Role)",
            description = "Đăng nhập bằng Email/Pass. <br/>" +
                    "1. **Cookies**: Hệ thống set `access_token` và `refresh_token` vào HttpOnly Cookies. <br/>" +
                    "2. **Body**: Trả về thông tin Role và trạng thái xác thực để Frontend điều hướng."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công (Cookies set + Body JSON)",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Sai email hoặc mật khẩu")
    })
    @PostMapping("/login")
    APIResponse<AuthenticationResponse> login (@RequestBody AuthenticateRequest request, HttpServletResponse httpServletResponse) throws JOSEException, ParseException {
        AuthenticationDTO result = authService.authentication(request);
        ResponseCookie accessToken = authCookieService.setAccessToken(result.accessToken());
        ResponseCookie refreshToken = authCookieService.setRefreshToken(result.refreshToken());

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, accessToken.toString());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshToken.toString());

        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .role(result.role())
                .authenticated(true)
                .build();

        return APIResponse.<AuthenticationResponse>builder()
                .message("Login successfully")
                .result(response)
                .build();
    }

    @Operation(
            summary = "Làm mới Token (Auto Cookie)",
            description = "Tự động đọc Cookie `refresh_token` từ trình duyệt. <br/>" +
                    "Nếu hợp lệ: Set lại cặp Cookie mới và trả về Role trong Body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refresh thành công, Cookie mới được set"),
            @ApiResponse(responseCode = "401", description = "Cookie không tồn tại hoặc Token hết hạn")
    })
    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        String refreshToken = resolveToken(httpServletRequest, "refresh_token");

        AuthenticationDTO result = authService.refreshToken(refreshToken);

        ResponseCookie at = authCookieService.setAccessToken(result.accessToken());
        ResponseCookie rt = authCookieService.setRefreshToken(result.refreshToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, at.toString());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, rt.toString());

        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .role(result.role())
                .authenticated(true)
                .build();

        return APIResponse.<AuthenticationResponse>builder()
                .result(response)
                .message("Refresh successfully")
                .build();
    }

    @Operation(
            summary = "Đăng xuất (Clear Cookies)",
            description = "Đọc `access_token` và `refresh_token` từ Cookie để blacklist, sau đó gửi lệnh xóa sạch Cookies về trình duyệt."
    )
    @PostMapping("/logout")
    APIResponse<?> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        String refreshToken = resolveToken(httpServletRequest, "refresh_token");

        ResponseCookie clearAccessCookie = authCookieService.clearCookie("access_token");
        ResponseCookie clearRefreshCookie = authCookieService.clearCookie("refresh_token");

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, clearAccessCookie.toString());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());

        authService.logout(refreshToken);
        return APIResponse.builder()
                .message("Log out successfully")
                .build();
    }

    private String resolveToken(HttpServletRequest request, String cookieName) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return authCookieService.getCookieValue(request, cookieName);
    }
}