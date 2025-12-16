package com.fo_product.api_gateway.custom;

import org.springframework.http.HttpCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ServerCookieBearerTokenConverter implements ServerAuthenticationConverter {
    private final ServerBearerTokenAuthenticationConverter defaultConverter = new ServerBearerTokenAuthenticationConverter();

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        // 1. Lấy token từ Cookie
        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("access_token"))
                .map(HttpCookie::getValue)
                .flatMap(token -> {
                    exchange.getRequest().mutate()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return defaultConverter.convert(exchange);
                })
                .switchIfEmpty(defaultConverter.convert(exchange));
    }
}
