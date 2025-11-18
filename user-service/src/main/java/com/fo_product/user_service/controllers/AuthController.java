package com.fo_product.user_service.controllers;

import com.fo_product.user_service.resources.APIResponse;
import com.fo_product.user_service.resources.requests.AuthenticateRequest;
import com.fo_product.user_service.resources.requests.TokenRequest;
import com.fo_product.user_service.resources.responses.AuthResponse;
import com.fo_product.user_service.services.interfaces.IAuthService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    IAuthService authService;

    @PostMapping("/login")
    APIResponse<AuthResponse> login (@RequestBody AuthenticateRequest request) {
        var response = authService.authentication(request);
        return APIResponse.<AuthResponse>builder()
                .result(response)
                .message("Login successfully")
                .build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthResponse> refreshToken(@RequestBody TokenRequest request) throws ParseException, JOSEException {
        var response = authService.refreshToken(request);
        return APIResponse.<AuthResponse>builder()
                .result(response)
                .message("Refresh successfully")
                .build();
    }

    @PostMapping("/logout")
    APIResponse<?> logout(@RequestBody TokenRequest request) throws ParseException, JOSEException {
        authService.logout(request);
        return APIResponse.<AuthResponse>builder()
                .message("Log out successfully")
                .build();
    }
}
