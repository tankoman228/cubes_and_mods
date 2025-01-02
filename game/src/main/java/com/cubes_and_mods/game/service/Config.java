package com.cubes_and_mods.game.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Data found in paths.properties
 * */
public class Config {

	public static String PATH_TO_BACKUPS;
	public static String PATH_TO_SERVERS;
	public static String ID_MACHINE_IN_DB;

	/** 
	 * Invoke in main function (ResApplication.java) only and once
	 * */
	public static void INIT() {
		
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("paths.properties"))) {
        	
            properties.load(input);
            PATH_TO_BACKUPS = properties.getProperty("PATH_TO_BACKUPS");
            PATH_TO_SERVERS = properties.getProperty("PATH_TO_SERVERS");
            ID_MACHINE_IN_DB = properties.getProperty("ID_MACHINE_IN_DB");
            
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
        } 
	}
}
