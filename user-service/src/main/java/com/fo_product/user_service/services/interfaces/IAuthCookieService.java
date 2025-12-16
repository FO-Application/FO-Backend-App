package com.fo_product.user_service.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface IAuthCookieService {
    ResponseCookie setAccessToken(String accessToken);
    ResponseCookie setRefreshToken(String refreshToken);
    ResponseCookie clearCookie(String cookieName);
    String getCookieValue(HttpServletRequest request, String cookieName);
}
