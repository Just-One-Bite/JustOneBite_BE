package com.delivery.justonebite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JustonebiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(JustonebiteApplication.class, args);
	}

}
