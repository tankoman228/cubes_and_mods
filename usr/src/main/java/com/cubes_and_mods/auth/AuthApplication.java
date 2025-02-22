package com.cubes_and_mods.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {		
        
        if (!PasswordHash.TEST_HASH()) { // Я тогда накосячил с проверкой хешей, не хочу убирать проверку
        	throw new RuntimeException("Не работает служба хеширования паролей!");
        }
		
		SpringApplication.run(AuthApplication.class, args);
		
        if (!PasswordHash.TEST_HASH()) {
        	throw new RuntimeException("Jopa");
        }
		
	}

}
