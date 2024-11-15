package com.cubes_and_mods.buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cubes_and_mods.buy.service_repos")
public class ResApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResApplication.class, args);
	}

}
