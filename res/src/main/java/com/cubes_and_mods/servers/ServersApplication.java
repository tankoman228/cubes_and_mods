package com.cubes_and_mods.servers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.cubes_and_mods.servers", 
		"com.cubes_and_mods.servers.sevice_repos", 
		"com.cubes_and_mods.servers.db"})  
@EnableJpaRepositories(basePackages = "com.cubes_and_mods.servers.service_repos")
public class ServersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServersApplication.class, args);
	}

}
