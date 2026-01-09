package com.fo_product.order_service.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${openapi.service.url}")
    private String url;

    @Bean
    public OpenAPI orderOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(url)))
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0")
                        .description("Order Service API Documentation")
                );
    }
}
