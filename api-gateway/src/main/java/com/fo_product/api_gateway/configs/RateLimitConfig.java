package com.fo_product.api_gateway.configs;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")) // 1. Lấy header
                        .map(header -> header.split(",")[0].trim()) // 2. Nếu có, lấy IP đầu tiên
                        .orElseGet(() -> Optional.ofNullable(exchange.getRequest().getRemoteAddress()) // 3. Nếu không, lấy remote address
                                .map(addr -> addr.getAddress().getHostAddress())
                                .orElse("unknown")) // 4. Fallback cuối cùng
        );
    }
}
