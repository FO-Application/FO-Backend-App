package com.fo_product.api_gateway.filters;

import java.util.ArrayList;
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

        // --- BƯỚC 1: TẠO HEADER MỚI VÀ COPY SÂU (DEEP COPY) ---
        HttpHeaders newHeaders = new HttpHeaders();

        // Thay vì dùng newHeaders.putAll(request.getHeaders()) -> Gây lỗi Shallow Copy
        // Ta duyệt qua từng phần tử và tạo một ArrayList MỚI cho từng value
        request.getHeaders().forEach((key, value) -> {
            // new ArrayList<>(value) chính là chìa khoá để "bẻ khoá" UnmodifiableList
            newHeaders.put(key, new ArrayList<>(value));
        });

        // --- BƯỚC 2: THÊM HEADER MỚI (Bây giờ thì thoải mái thêm sửa xoá) ---
        if (isRefreshPath) {
            if (rt != null) {
                // Dùng set để ghi đè, tránh bị trùng lặp header nếu chạy qua filter nhiều lần
                newHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + rt);
            }
            if (at != null) {
                newHeaders.set("X-Access-Token", at);
            }
        } else {
            if (at != null) {
                newHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + at);
            }
            if (rt != null) {
                newHeaders.set("X-Refresh-Token", rt);
            }
        }

        // --- BƯỚC 3: DÙNG DECORATOR ĐỂ TRẢ VỀ HEADER MỚI ---
        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return newHeaders;
            }
        };

        // --- BƯỚC 4: TIẾP TỤC CHAIN VỚI REQUEST ĐÃ BỌC ---
        return chain.filter(exchange.mutate().request(decoratedRequest).build());
    }

    @Override
    public int getOrder() {
        return -101;
    }
}