package com.fo_product.payment_service.configs;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Slf4j
public class FeignClientInterceptorConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 1. Lấy Request hiện tại (đang gọi vào Payment Service) thông qua RequestContextHolder
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 2. Lấy chuỗi Authorization (Bearer eyJhb...) từ header của request gốc
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && !authHeader.isEmpty()) {
                    // 3. Nhét nguyên chuỗi đó vào header của Feign Request để gửi sang Order Service
                    requestTemplate.header("Authorization", authHeader);
                    log.info("Đã forward Authorization header sang Feign call");
                } else {
                    log.warn("Request gốc không có Authorization header!");
                }
            } else {
                // Trường hợp này xảy ra nếu gọi Feign trong một thread async hoặc background job không gắn với request
                log.warn("Không tìm thấy Request Attributes (có thể do chạy Async?)");
            }
        };
    }
}