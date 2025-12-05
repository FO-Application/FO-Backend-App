package com.fo_product.notification_service;

import com.fo_product.common_lib.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(
		scanBasePackageClasses = {
				NotificationServiceApplication.class,
				GlobalExceptionHandler.class
		}

)
@EnableAsync
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
