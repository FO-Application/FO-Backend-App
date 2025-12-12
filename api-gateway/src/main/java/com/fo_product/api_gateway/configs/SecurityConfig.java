package com.fo_product.api_gateway.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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
    protected String SECRET_KEY;

    @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithms}")
    protected String ALGORITHM;

    private static final String[] REQUEST_MATCHERS = {
            "/api/v1/auth/**",
            "/api/v1/user/**",
            "/api/v1/cuisine/**",
            "/api/v1/restaurant/**",
            "/api/v1/restaurant-schedule/**",
            "/v3/api-docs/**",      // Để Gateway lấy JSON từ các service con
            "/swagger-ui.html",     // Trang giao diện chính
            "/swagger-ui/**",       // Các file css, js của giao diện Swagger
            "/webjars/**",          // Thư viện giao diện (nếu cần)
            "/eureka/**"
    };

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource))

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(REQUEST_MATCHERS).permitAll()
                        .anyExchange().authenticated())

                .oauth2ResourceServer(
                        oauth2 -> oauth2
                                .jwt(jwtSpec -> jwtSpec
                                        .jwtDecoder(jwtDecoder())
                                        .jwtAuthenticationConverter(reactiveJwtAuthenticationConverter())
                        )
                ).build();
    }

    /**
     * Bean này là TRÁI TIM của việc xác thực.
     * Nó nói cho Spring Security biết cách giải mã token
     * dùng secret_key và thuật toán HS512 của bạn.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
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
