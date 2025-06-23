package com.cubes_and_mods.web;

import org.springframework.stereotype.Component;

@Deprecated
@Component
public class ProxyConfig {

    //TODO: удалить устаревгшие адреса
	private String local = "https://localhost:8080";
	
    private String usr = "http://localhost:8089/usr";
	
    private String res = "http://localhost:8089/res";
	
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
