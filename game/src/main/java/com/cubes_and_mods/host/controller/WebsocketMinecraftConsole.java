package com.cubes_and_mods.host.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cubes_and_mods.host.service.ServiceHandlers;
import com.cubes_and_mods.host.service.mineserver_process.IMinecraftHandler;

/**
 * Console of minecraft server, that is being PROXIED from process 
 * First message is ID of minecraft server
 * 
 * Look to mineserver_process package for this complex logic of child processes
 * */
@Component
public class WebsocketMinecraftConsole /*extends TextWebSocketHandler */{
	/*
	private boolean firstMessage; // Is it the first message is ID of mineserver
	private IMinecraftHandler handler; // Handler for CURRENT SOCKET, contains minecraft server object from DB
	
	@Autowired
	private ServiceHandlers ServiceHandlers;
	
	
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {	
        System.out.println("Session opened: " + session.getId());
        
        firstMessage = true;
        handler = null;
        sendMessage(session, "I'm alive, let's find some cute toads or rabbits! Send me ID of your server");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        
    	System.out.println("Message received: " + message.getPayload()); 
    	
    	// Init socket variables 
    	if (firstMessage) { 		
    		try {   			
    			Integer id = Integer.parseInt(message.getPayload());
    			handler = ServiceHandlers.get(id);
    			
    			if (!handler.isLaunched()) {
    				throw new Exception("NO SERVER WITH SUCH ID");
    			}
    			handler.trySubscribeToConsoleOutput(msg -> {
    				sendMessage(session, msg);
    			});
    			firstMessage = false;
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			session.sendMessage(new TextMessage(e.getMessage()));
    			session.close();
    		}
    	}
    	// Send message to minecraft server handler
    	else {
    		try {
    			session.sendMessage(new TextMessage(
    				handler.sendMessage(message.getPayload())));
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			session.sendMessage(new TextMessage(e.getMessage()));
    		}
    	}
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {	
        sendMessage(session, "Session closed. Rabbits and toads ran away! Упрыгали!");
    }

    private void sendMessage(WebSocketSession session, String message) {
    	
        //System.out.println(message);
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
