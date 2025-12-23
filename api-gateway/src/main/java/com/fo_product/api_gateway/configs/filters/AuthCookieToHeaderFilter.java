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

    private final List<String> REFRESH_TOKEN_PATHS = List.of("/auth/logout", "/auth/refresh");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        boolean isRefreshPath = REFRESH_TOKEN_PATHS.stream().anyMatch(path::endsWith);

        HttpCookie accessCookie = exchange.getRequest().getCookies().getFirst("access_token");
        HttpCookie refreshCookie = exchange.getRequest().getCookies().getFirst("refresh_token");

        String at = (accessCookie != null) ? accessCookie.getValue() : null;
        String rt = (refreshCookie != null) ? refreshCookie.getValue() : null;

        // CÁCH MỚI: Dùng mutate().request(consumer) cực kỳ an toàn cho Multipart
        return chain.filter(exchange.mutate().request(builder -> {
            if (isRefreshPath) {
                if (rt != null) builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + rt);
                if (at != null) builder.header("X-Access-Token", at);
            } else {
                if (at != null) builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + at);
                if (rt != null) builder.header("X-Refresh-Token", rt);
            }
            // Tuyệt đối không gọi builder.contentType(...) ở đây để tránh mất boundary
        }).build());
    }

    @Override
    public int getOrder() {
        return -101;
    }
}
