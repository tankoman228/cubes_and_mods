package com.cubes_and_mods.buy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Spring ломает ЖЦ и не даёт делать статические конструкторы.
 * Я знаю, это велосипед! Но нет времени переделывать, оно работает
 * */
public class ConfigNetwork {

	public static String GATEWAY;
	
	private static boolean alreadyConfigured = false;
	
	public static void INIT_CONFIG() {
		
		if (alreadyConfigured)
			return;
		
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("network.properties"))) {
        	
            properties.load(input);
            GATEWAY = properties.getProperty("gateway");

        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
        } 
        
        alreadyConfigured = true;
	}
}
