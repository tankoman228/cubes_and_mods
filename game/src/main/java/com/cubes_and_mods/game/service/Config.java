package com.cubes_and_mods.game.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

public class Config {

	public static String PATH_TO_BACKUPS;
	public static String PATH_TO_SERVERS;
	
	public static void INIT_CONFIG() {
		
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("paths.properties"))) {
        	
            properties.load(input);
            PATH_TO_BACKUPS = properties.getProperty("PATH_TO_BACKUPS");
            PATH_TO_SERVERS = properties.getProperty("PATH_TO_SERVERS");
            
            String error =  properties.getProperty("xPATH_TO_SERVERS");
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
        } 
	}
}
