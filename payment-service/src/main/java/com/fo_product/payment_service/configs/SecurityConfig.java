package com.fo_product.payment_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Quan trọng: nếu không tắt, các lệnh POST/PUT sẽ bị chặn)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Tắt Form Login mặc định (Để không bị redirect sang trang /login)
                .formLogin(AbstractHttpConfigurer::disable)

                // 3. Tắt HTTP Basic Auth (Để trình duyệt không hiện popup nhập mật khẩu)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. Cho phép mọi request đi qua (Permit All)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
