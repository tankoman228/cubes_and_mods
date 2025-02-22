package com.cubes_and_mods.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
@ComponentScan(basePackages = {
		"com.cubes_and_mods.auth",
		"com.cubes_and_mods.auth.controller",
		"com.cubes_and_mods.auth.jpa",
		"com.cubes_and_mods.auth.jpa.repos"

		})*/
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {		
        
        if (!PasswordHash.TEST_HASH()) { // Я тогда накосячил с проверкой хешей, не хочу убирать проверку
        	throw new RuntimeException("Не работает служба хеширования паролей!");
        }
		
		SpringApplication.run(AuthApplication.class, args);			
	}
}
