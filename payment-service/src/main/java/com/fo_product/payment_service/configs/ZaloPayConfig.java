package com.fo_product.payment_service.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZaloPayConfig {
    @Value("${zalo-pay.app-id}")
    Integer appId;

    @Value("${zalo-pay.key1}")
    String key1;

    @Value("${zalo-pay.key2}")
    String key2;

    @Value("${zalo-pay.create-order-endpoint}")
    String createOrderEndpoint;
}
