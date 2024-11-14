package com.cubes_and_mods.game.controller;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cubes_and_mods.game.db.Mineserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class WebsocketConsole extends TextWebSocketHandler {
	
	private PrintWriter processWriter;
	private Map<Integer, Mineserver> openedMineservers = new HashMap<>();
	
	private boolean firstMessage = true;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
    	
        System.out.println("Session opened: " + session.getId());
        sendMessage(session, "Connected to Minecraft Server Manager!");

        if (processWriter == null) {
            try {/*
                processWriter = new PrintWriter(ServerFilesManager.serverProcess.getOutputStream(), true);
                streamProcessOutput(ServerFilesManager.serverProcess, session);*/
            } catch (Exception e) {
                sendMessage(session, "Femboy Error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        
    	System.out.println("Message received: " + message.getPayload());
    	
    	if (firstMessage) {
    		
    		try {
    			int id = Integer.parseInt(message.getPayload());
    			var mineserver = 0;
    		}
    		catch (Exception e) {
    			session.sendMessage(new TextMessage(e.getMessage()));
    			session.close();
    		}
    	}
    	else {
    		
    	}
/*
        if (ServerFilesManager.serverProcess == null) {
            sendMessage(session, "Server process is not running.");
            return;
        }*/

        sendCommandToProcess(message.getPayload(), session);
    }

    private void sendCommandToProcess(String command, WebSocketSession session) {
        try {
            if (processWriter != null) {
                processWriter.println(command);
                processWriter.flush();
                sendMessage(session, "Command sent: " + command);
            }
            else {/*
                processWriter = new PrintWriter(ServerFilesManager.serverProcess.getOutputStream(), true);
                streamProcessOutput(ServerFilesManager.serverProcess, session);*/
            }
        } catch (Exception e) {
            sendMessage(session, "Error sending command: " + e.getMessage());
        }
    }

    private void streamProcessOutput(Process process, WebSocketSession session) {
        /*new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sendMessage(session, line);
                }
            } catch (IOException e) {
                sendMessage(session, "Error reading server output: " + e.getMessage());
            }
        }).start();*/
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        sendMessage(session, "Session closed.");/*
        if (ServerFilesManager.serverProcess != null) {
            ServerFilesManager.serverProcess.destroy();
            ServerFilesManager.serverProcess = null;
        }*/
    }

    private void sendMessage(WebSocketSession session, String message) {
        /*System.out.println(message);
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
