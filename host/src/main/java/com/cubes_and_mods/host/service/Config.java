package com.cubes_and_mods.host.service;

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
	public static Integer ID_MACHINE_IN_DB;

	/** 
	 * Invoke in main function (HostApplication.java) only and once
	 * */
	public static void INIT() {
		
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("paths.properties"))) {
        	
            properties.load(input);
            PATH_TO_BACKUPS = properties.getProperty("PATH_TO_BACKUPS");
            PATH_TO_SERVERS = properties.getProperty("PATH_TO_SERVERS");
            ID_MACHINE_IN_DB = Integer.valueOf(properties.getProperty("ID_MACHINE_IN_DB"));
            
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("cannot init Config class in package com.cubes_and_mods.host.service");
        } 
        
        System.out.println("Path to backups: " + PATH_TO_BACKUPS);
        System.out.println("Path to servers: " + PATH_TO_SERVERS);
        
        if (PATH_TO_BACKUPS == null || PATH_TO_SERVERS == null) {
        	throw new RuntimeException("Wrong path.properties. Maybe wrong order of paths or undefined values?");
        }
	}
}
