package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.services.interfaces.IAuthCookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthCookieService implements IAuthCookieService {
    @Value("${jwt.access-token-expiration}")
    long ACCESS_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expiration}")
    long REFRESH_TOKEN_EXPIRATION_TIME;

    @Override
    public ResponseCookie setAccessToken(String accessToken) {
        return ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(ACCESS_EXPIRATION_TIME)
                .sameSite("Lax")
                .build();
    }

    @Override
    public ResponseCookie setRefreshToken(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(REFRESH_TOKEN_EXPIRATION_TIME)
                .sameSite("Lax")
                .build();
    }

    @Override
    public ResponseCookie clearCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    @Override
    public String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
