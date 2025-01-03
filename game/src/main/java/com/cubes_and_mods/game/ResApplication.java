package com.cubes_and_mods.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cubes_and_mods.game.service.Config;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.cubes_and_mods.game", 
		"com.cubes_and_mods.game.repos", 
		"com.cubes_and_mods.game.service", 
		"com.cubes_and_mods.game.service.mineserver_process",
		"com.cubes_and_mods.game.controller", 
		"com.cubes_and_mods.game.db"
})
public class ResApplication {

	public static void main(String[] args) {
		
		Config.INIT();
		
		SpringApplication.run(ResApplication.class, args);
	}
}
