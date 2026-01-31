package com.fo_product.delivery_service.configs;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@Slf4j
public class FeignClientInterceptorConfig {
    //Cấu hình này sẽ giúp cho việc giao tiếp giữa các service sẽ không bị chặn bơi các filter của security do đã gắn token lên header authoriation
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                // Cách lấy Token CHUẨN khi dùng OAuth2 Resource Server
                if (authentication instanceof JwtAuthenticationToken jwtToken) {
                    String tokenValue = jwtToken.getToken().getTokenValue();
                    requestTemplate.header("Authorization", "Bearer " + tokenValue);
                }
                // Fallback: Nếu không phải JWT (ví dụ chạy test)
                else if (authentication.getCredentials() instanceof String tokenStr) {
                    requestTemplate.header("Authorization", "Bearer " + tokenStr);
                }
            } else {
                log.warn("Feign Call: Không tìm thấy Authentication trong SecurityContext!");
            }
        };
    }
}
