package com.cubes_and_mods.game.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


public class FilesWebClient {

    public static final int FILE_CHUNK_SIZE = 8192;
	
    private String URI;
    private WebSocketClient webSocketClient;
    private boolean down = false;
    private File downloadTo;
    private FileOutputStream fos;

    public FilesWebClient(String uri) {
        this.URI = uri;
        connect();
    }

    private void connect() {
        try {
            webSocketClient = new WebSocketClient(new URI(URI)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to server");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received message: " + message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    if (down) {
                    	try {
							fos.write(bytes.array());
						} catch (IOException e) {						
							e.printStackTrace();
						}
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection closed: " + reason);
                    
                    if (down) {
                    	try {
							fos.close();
						} catch (IOException e) {						
							e.printStackTrace();
						}
                    }
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendFile(String path, File file) {
    	
    	down = false;
    	webSocketClient.send("Upload");
    	webSocketClient.send(path);
    	
        try (FileInputStream fis = new FileInputStream(file)) {
        	
            byte[] fileBytes = new byte[FILE_CHUNK_SIZE];
            
            while (-1 != fis.readNBytes(fileBytes, 0, FILE_CHUNK_SIZE)) {
            	webSocketClient.send(fileBytes);
            }          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GetFile(String path, File file) {
    	
    	down = true;
    	webSocketClient.send("Download");
    	webSocketClient.send(path);
    }
}
