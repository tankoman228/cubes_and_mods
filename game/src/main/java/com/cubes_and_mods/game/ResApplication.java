package com.cubes_and_mods.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cubes_and_mods.game.service.Config;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = {"service_repos.repos"})
public class ResApplication {

	public static void main(String[] args) {
		
		Config.INIT();
		
		SpringApplication.run(ResApplication.class, args);
	}
}
