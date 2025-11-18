package com.fo_product.api_gateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithm}")
    protected String ALGORITHM;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        // Cho phép các endpoint public của user-service
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/eureka/**").permitAll()
                        // Tất cả các request khác phải được xác thực
                        .anyExchange().authenticated())

                // KÍCH HOẠT "FILTER" XÁC THỰC TOKEN TẠI ĐÂY
                // Nó sẽ tự động sử dụng Bean JwtDecoder ở dưới
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
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
}
