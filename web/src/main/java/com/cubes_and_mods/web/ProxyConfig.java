package com.cubes_and_mods.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxyConfig {
	
	//Конфиг не запахал, ну и пожалуйста, ну и не нужно!
	
	@Value("${local}")
	private String local = "http://localhost:8080";
	
	@Value("${services.usr}")
    private String usr = "http://localhost:8089/usr";
	
	@Value("${services.res}")
    private String res = "http://localhost:8089/res";
	
	@Value("${services.buy}")
    private String buy = "http://localhost:8089/buy";
	
	private String gameUri = null;
	
    public String getLocal(){
        return local;
    }
    
    public String getUsr(){
        return usr;
    }
    
    public String getRes() {
    	return res;
    }

    public String getBuy() {
    	return buy;
    }
}
