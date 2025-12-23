package com.fo_product.api_gateway.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithms}")
    private String ALGORITHM;

//    private static final String[] REQUEST_MATCHERS = {
//            "/api/v1/auth/**",
//            "/api/v1/cuisine/**",
//            "/api/v1/restaurant/**",
//            "/api/v1/restaurant-schedule/**",
//            "/api/v1/category/**",
//            "/api/v1/product/**",
//            "/api/v1/option-group/**",
//            "/api/v1/option-item/**",
//            "/v3/api-docs/**",      // Để Gateway lấy JSON từ các service con
//            "/swagger-ui.html",     // Trang giao diện chính
//            "/swagger-ui/**",       // Các file css, js của giao diện Swagger
//            "/webjars/**",          // Thư viện giao diện (nếu cần)
//            "/eureka/**"
//    };

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource))

                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())

                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .jwt(jwtSpec -> jwtSpec
                                        .jwtDecoder(jwtDecoder())
                                        .jwtAuthenticationConverter(reactiveJwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        return NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.from(ALGORITHM))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter));
        return jwtAuthenticationConverter;
    }
}
