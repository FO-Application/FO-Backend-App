package com.fo_product.payment_service.configs;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientInterceptorConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() != null) {
                // Lấy token từ credentials (thường là String token)
                String token = authentication.getCredentials().toString();

                // Nếu là JwtAuthenticationToken, có thể lấy trực tiếp
                if (authentication instanceof JwtAuthenticationToken jwt) {
                    token = jwt.getToken().getTokenValue();
                }

                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}