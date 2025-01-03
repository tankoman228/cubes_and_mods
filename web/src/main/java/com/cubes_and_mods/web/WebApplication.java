package com.cubes_and_mods.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cubes_and_mods.web", 
		"com.cubes_and_mods.web.Controllers", 
		"com.cubes_and_mods.web.DB",
		"com.cubes_and_mods.web.Services",
		"com.cubes_and_mods.web.Clients",
		"com.cubes_and_mods.web.WebSockets",})  
@EntityScan(basePackages = {"com.cubes_and_mods.web.DB"})
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
