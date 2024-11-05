package com.cubes_and_mods.res;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.cubes_and_mods.res", 
		"com.cubes_and_mods.res.sevice_repos", 
		"com.cubes_and_mods.res.db"})  
@EnableJpaRepositories(basePackages = "com.cubes_and_mods.res.service_repos")
public class ResApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResApplication.class, args);
	}

}
