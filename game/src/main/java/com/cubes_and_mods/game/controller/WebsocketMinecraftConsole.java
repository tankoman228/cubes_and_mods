package com.cubes_and_mods.game.controller;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.repos.ReposTariff;
import com.cubes_and_mods.game.repos.ReposVersion;
import com.cubes_and_mods.game.service.mineserver_process.IMinecraftHandler;
import com.cubes_and_mods.game.service.mineserver_process.ITextCallback;
import com.cubes_and_mods.game.service.mineserver_process.MinecraftHandler;
import com.cubes_and_mods.game.service.mineserver_process.MinecraftServerObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Console of minecraft server, that was PROXIED from process. Logic see in mineserver_process package
 * */
@Component
public class WebsocketMinecraftConsole extends TextWebSocketHandler {
	
	/**
	 * Handled mineservers with its' threads and processes for ALL SOCKETS
	 * */
	public static volatile Map<Integer, IMinecraftHandler> HANDLED = new HashMap<>(); 
	
	private boolean firstMessage = true; // The first message is ID of mineserver
	private IMinecraftHandler handler; // Handler for CURRENT SOCKET
	private Mineserver mineserver; // Mineserver for CURRENT HANDLER
	
	@Autowired
	private ReposMineserver mineservers;
	
	@Autowired
	private ReposTariff tariffs;
	
	
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {	
        System.out.println("Session opened: " + session.getId());
        sendMessage(session, "I'm alive, let's find some cute toads or rabbits! Send me ID of your server");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        
    	System.out.println("Message received: " + message.getPayload()); 
    	
    	// Init or get handler
    	if (firstMessage) { 		
    		try {
    			
    			Integer id = Integer.parseInt(message.getPayload());
    			
    			if (HANDLED.containsKey(id)) {
    				handler = HANDLED.get(id);
    				mineserver = handler.getMineserver();
    			}
    			else {
    				
    				throw new Exception("NOT FOUND LAUNCHED SERVER WITH SUCH ID");
    				
    				
    				//handler = new MinecraftHandler(mineserver, "sh run.sh");
    				//new MinecraftServerObserver(handler, tariffs, mineservers);
    				//HANDLED.put(id, handler);
    			}
    			handler.trySubscribeToConsoleOutput(msg -> {
    				sendMessage(session, msg);
    			});
    		}
    		catch (Exception e) {
    			session.sendMessage(new TextMessage(e.getMessage()));
    			session.close();
    		}
    	}
    	// Send message to handler
    	else {
    		session.sendMessage(new TextMessage(
    				handler.sendMessage(message.getPayload())));
    	}
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {	
        sendMessage(session, "Session closed. Rabbits and toads ran away! Упрыгали!");
    }

    private void sendMessage(WebSocketSession session, String message) {
    	
        System.out.println(message);
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
