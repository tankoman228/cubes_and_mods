package com.cubes_and_mods.web;

public class ProxyConfig {
	
	//Конфиг не запахал, ну и пожалуйста, ну и не нужно!
	private static String local = "http://localhost:8080";
    private static String usr = "http://localhost:8089/usr";
    private static String res = "http://localhost:8089/res";
    private static String buy = "http://localhost:8089/buy";
    private static String game = "http://localhost:8083";
    private static String console = "ws://localhost:8083/console";
    
    public static String getLocal(){
        return local;
    }
    
    public static String getUsr(){
        return usr;
    }
    
    public static String getRes() {
    	return res;
    }

    public static String getBuy() {
    	return buy;
    }
    
    public static String getGame() {
    	return game;
    }
    
    public static String getConsole() {
    	return console;
    }
}