package com.cubes_and_mods.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RequestType {

	@JsonIgnore
	public static final String basePath = "localhost:8089/";
	
	public String header;
	public String defaultJsonRequest; 
	public String method;
	public String path;
	public String contentType;
}
