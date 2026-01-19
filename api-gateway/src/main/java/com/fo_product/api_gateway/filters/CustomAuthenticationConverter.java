package com.fo_product.api_gateway.filters;

import org.springframework.http.HttpCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class CustomAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String COOKIE_NAME = "access_token";
    private final ServerBearerTokenAuthenticationConverter headerConverter = new ServerBearerTokenAuthenticationConverter();

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        //Lấy access token trong cookie
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);

        //Kiểm tra cookie nếu có thì dùng luôn
        if (cookie != null) {
            return Mono.just(new BearerTokenAuthenticationToken(cookie.getValue()));
        }

        //Không thì gọi converter mặc định, tự tìm header Authorization: Bearer
        return headerConverter.convert(exchange);
    }
}
