package com.fo_product.merchant_service.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {
    //Cấu hình áp dụng cho các microservice
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilience4JCircuitBreakerFactoryCustomizer() {

        //1. Cấu hình Circuit Breaker(Luật đóng mở cầu dao)
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) //Tính trượt theo số lượng
                .slidingWindowSize(10) //Theo dõi 10 request gần nhất
                .failureRateThreshold(50.0f) //Nếu 50% (5 request) lỗi -> ngắt mạch(OPEN)
                .waitDurationInOpenState(Duration.ofSeconds(10)) //Ngắt trong 10s rồi mới thử lại (HALF-OPEN)
                .permittedNumberOfCallsInHalfOpenState(3) //Khi thử lại, cho phép 3 request đi qua
                .build();



        //3. Gắn cấu hình vào Factory
        return factory -> factory.configureDefault(
                config -> new Resilience4JConfigBuilder(config)

                        .circuitBreakerConfig(circuitBreakerConfig)
                        .build()
        );
    }
}
