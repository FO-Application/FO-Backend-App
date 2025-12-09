package com.fo_product.merchant_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
		scanBasePackages = "com.fo_product"
)
@EnableCaching
public class MerchantServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MerchantServiceApplication.class, args);
	}

}
