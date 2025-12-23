package com.fo_product.api_gateway.configs.filters;

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

import java.util.List;

@Component
public class AuthCookieToHeaderFilter implements GlobalFilter, Ordered {

    private final List<String> REFRESH_TOKEN_PATHS = List.of(
            "/auth/logout",
            "/auth/refresh"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        boolean isRefreshPath = REFRESH_TOKEN_PATHS.stream().anyMatch(path::endsWith);

        HttpCookie accessCookie = exchange.getRequest().getCookies().getFirst("access_token");
        HttpCookie refreshCookie = exchange.getRequest().getCookies().getFirst("refresh_token");

        ServerHttpRequest decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders writableHeaders = new HttpHeaders();
                writableHeaders.addAll(super.getHeaders());

                String at = (accessCookie != null) ? accessCookie.getValue() : null;
                String rt = (refreshCookie != null) ? refreshCookie.getValue() : null;

                // LOGIC HOÁN ĐỔI:
                if (isRefreshPath) {
                    // Với Logout/Refresh: Ưu tiên Refresh Token vào Authorization
                    if (rt != null) {
                        writableHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + rt);
                    }
                    if (at != null) {
                        writableHeaders.set("X-Access-Token", at);
                    }
                } else {
                    // Với các API thường: Ưu tiên Access Token vào Authorization
                    if (at != null) {
                        writableHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + at);
                    }
                    if (rt != null) {
                        writableHeaders.set("X-Refresh-Token", rt);
                    }
                }

                return HttpHeaders.readOnlyHttpHeaders(writableHeaders);
            }
        };

        return chain.filter(exchange.mutate().request(decorator).build());
    }

    @Override
    public int getOrder() {
        return -101; // Chạy sớm nhất
    }
}