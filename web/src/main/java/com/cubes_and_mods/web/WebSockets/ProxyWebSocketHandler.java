package com.cubes_and_mods.web.WebSockets;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> clientSessions = new ArrayList<>();
    private Map<WebSocketSession, WebSocketSession> targetSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clientSessions.add(session);
        connectToTargetServer(session);
    }

    private void connectToTargetServer(WebSocketSession clientSession) {
        StandardWebSocketClient client = new StandardWebSocketClient();
        
        TargetWebSocketHandler targetHandler = new TargetWebSocketHandler(clientSession);
        
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        
        //TODO: убрать захардкоженое значение
        client.doHandshake(targetHandler, headers, URI.create("ws://localhost:8083/console"));
        try {
			clientSession.sendMessage(new TextMessage("Удачное подключение"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketSession targetSession = targetSessions.get(session);
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clientSessions.remove(session);
        WebSocketSession targetSession = targetSessions.remove(session);
        if (targetSession != null) {
            targetSession.close(); 
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error: " + exception.getMessage());
        
        clientSessions.remove(session);
        
        WebSocketSession targetSession = targetSessions.remove(session);
        if (targetSession != null) {
            try {
                targetSession.close();
                System.out.println("Closed target session due to transport error.");
            } catch (Exception e) {
                System.err.println("Error closing target session: " + e.getMessage());
            }
        }
        
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("Error occurred: " + exception.getMessage()));
            } catch (Exception e) {
                System.err.println("Error sending message to client: " + e.getMessage());
            }
        }
    }

    private class TargetWebSocketHandler extends TextWebSocketHandler {
        
        private WebSocketSession clientSession;

        public TargetWebSocketHandler(WebSocketSession clientSession) {
            this.clientSession = clientSession; 
            targetSessions.put(clientSession, null); 
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            targetSessions.put(clientSession, session); 
            System.out.println("Connected to target server.");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            if (clientSession.isOpen()) {
                try {
                    clientSession.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("Error sending message to client: " + e.getMessage());
                }
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            System.out.println("Disconnected from target server.");
            
            if (clientSession.isOpen()) {
                try {
                    clientSession.close(new CloseStatus(1000, "Target server closed the connection."));
                } catch (Exception e) {
                    System.err.println("Error closing client session: " + e.getMessage());
                }
            }
            
            targetSessions.remove(clientSession); 
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("Transport error from target server: " + exception.getMessage());
            
            if (clientSession.isOpen()) {
                try {
                    clientSession.close(new CloseStatus(1006, "Target server error: " + exception.getMessage()));
                } catch (Exception e) {
                    System.err.println("Error closing client session: " + e.getMessage());
                }
            }
            
            clientSessions.remove(clientSession);
            targetSessions.remove(clientSession);
        }
    }
}