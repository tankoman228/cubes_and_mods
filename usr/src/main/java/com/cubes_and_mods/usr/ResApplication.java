package com.cubes_and_mods.usr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResApplication {

	public static void main(String[] args) {		
        
        if (!PasswordHash.TEST_HASH()) { // Я тогда накосячил с проверкой хешей, не хочу убирать проверку
        	throw new RuntimeException("Не работает служба хеширования паролей!");
        }
		
		SpringApplication.run(ResApplication.class, args);
		
        if (!PasswordHash.TEST_HASH()) {
        	throw new RuntimeException("Jopa");
        }
		
	}

}
