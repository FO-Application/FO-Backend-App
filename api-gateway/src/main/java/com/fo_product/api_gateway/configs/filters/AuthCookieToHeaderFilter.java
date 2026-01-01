package com.fo_product.api_gateway.configs.filters;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthCookieToHeaderFilter implements GlobalFilter, Ordered {

    private final List<String> REFRESH_TOKEN_PATHS = List.of("/auth/logout", "/auth/refresh");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        boolean isRefreshPath = REFRESH_TOKEN_PATHS.stream().anyMatch(path::endsWith);

        HttpCookie accessCookie = request.getCookies().getFirst("access_token");
        HttpCookie refreshCookie = request.getCookies().getFirst("refresh_token");

        String at = (accessCookie != null) ? accessCookie.getValue() : null;
        String rt = (refreshCookie != null) ? refreshCookie.getValue() : null;

        // 1. Tạo một HttpHeaders mới (Mutable - có thể sửa đổi)
        HttpHeaders newHeaders = new HttpHeaders();
        // 2. Copy toàn bộ header cũ sang header mới
        newHeaders.putAll(request.getHeaders());

        // 3. Thêm header mới vào list này (An toàn tuyệt đối)
        if (isRefreshPath) {
            if (rt != null) newHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + rt);
            if (at != null) newHeaders.add("X-Access-Token", at);
        } else {
            if (at != null) newHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + at);
            if (rt != null) newHeaders.add("X-Refresh-Token", rt);
        }

        // 4. Dùng Decorator để "đánh tráo" headers khi hệ thống gọi getHeaders()
        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return newHeaders;
            }
        };

        // 5. Tiếp tục filter chain với request đã được bọc (decorated)
        return chain.filter(exchange.mutate().request(decoratedRequest).build());
    }

    @Override
    public int getOrder() {
        return -101;
    }
}