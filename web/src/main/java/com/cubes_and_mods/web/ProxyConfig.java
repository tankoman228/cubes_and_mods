package com.cubes_and_mods.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxyConfig {
	
	//Конфиг не запахал, ну и пожалуйста, ну и не нужно!

    //TODO: удалить устаревгшие адреса, кроме local
	private String local = "https://192.168.0.103:8080";
	
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
