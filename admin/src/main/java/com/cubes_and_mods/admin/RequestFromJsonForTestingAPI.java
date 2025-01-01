package com.cubes_and_mods.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Just for parsing JSON
 * */
public class RequestFromJsonForTestingAPI {
	
	public String header;
	public String defaultBody; 
	public String method;
	public String path;
	public String contentType;
	public String description = "";
}