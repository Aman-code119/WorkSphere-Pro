package com.Example.INTERNAL_EMAIL_SERVICE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.Example.INTERNAL_EMAIL_SERVICE")
public class InternalEmailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternalEmailServiceApplication.class, args);
	}

}
