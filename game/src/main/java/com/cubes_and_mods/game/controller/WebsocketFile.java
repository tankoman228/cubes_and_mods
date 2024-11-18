package com.cubes_and_mods.game.controller;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebsocketFile extends BinaryWebSocketHandler {
	
	public static final int CHUNK_SIZE = 1024 * 1024;
	
	private int stage = 0;
	private String path;
	private boolean Download;

	@Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        
		// First 2 messages - initialize session
		switch (stage) {
		case 0:
			String msg = message.getPayload().toString(); 
			stage++;
			Download = msg.equalsIgnoreCase("download");
			return;
		case 1:
			String msg_ = message.getPayload().toString(); 
			stage++;
			path = msg_;
			if (!Download) {
				Files.createDirectories(Path.of(path));
				Files.createFile(Path.of(path));
			}
			return;
			default: break;
		}
		
		// Other messages are sending or getting data
		File file = new File(path);
		if (Download) {
			
			 try (InputStream inputStream = new FileInputStream(file)) {		 
		            byte[] buffer = new byte[CHUNK_SIZE];
		            int bytesRead;
		            while ((bytesRead = inputStream.read(buffer)) != -1) {
		                session.sendMessage(new BinaryMessage(buffer, 0, bytesRead, false));
		            }
		            session.close(CloseStatus.NORMAL);		         
		        }
		}
		else {	
			try (OutputStream os = new FileOutputStream(file, true)) {
				os.write(message.getPayload().array());
			}
		}
    }
}
