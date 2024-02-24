package com.gateway.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class EcomApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcomApiGatewayApplication.class, args);
	}

	@RestController
	public class HelloController {
		@GetMapping("/")
		public String hello() {
			return "hello";
		}
	}

}
