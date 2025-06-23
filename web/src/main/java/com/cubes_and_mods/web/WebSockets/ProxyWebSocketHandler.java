package com.cubes_and_mods.web.WebSockets;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.web_clients.game.ServiceAddressKeeper;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ProxyWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> clientSessions = new ArrayList<>();
    private Map<WebSocketSession, WebSocketSession> targetSessions = new HashMap<>();

    ServiceAddressKeeper Ips = new ServiceAddressKeeper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clientSessions.add(session);
        connectToTargetServer(session);
    }

    private String getAdres(WebSocketSession session) {
        URI uri = session.getUri();
        int id = -1;
        String token = null, IP = null;

        if (uri != null && uri.getQuery() != null) {
            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("id")) {
                    if(pair[0].equals("id")){
                        System.out.println("ws id: " + pair[1]);
                        id = Integer.parseInt(pair[1]);
                    }
                    else if (pair[0].equals("token")){
                        System.out.println("ws token: " + pair[1]);
                        token = pair[1];
                    }
                    IP = Ips.getIp(id, token).block();
                }
            }
        }
        System.out.println("Для подключения по веб сокету: " + IP);
        return IP; // id не найден
    }

    private void connectToTargetServer(WebSocketSession clientSession) {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (InputStream trustStoreStream = ClientConnectorForKey.class.getClassLoader()
                    .getResourceAsStream("clientTrustStorehost.jks")) {
                if (trustStoreStream == null) {
                    throw new FileNotFoundException("Truststore not found in resources");
                }
                trustStore.load(trustStoreStream, "yourpassword".toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Инициализируем WebSocket клиента
            StandardWebSocketClient client = new StandardWebSocketClient();
            client.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);

            TargetWebSocketHandler targetHandler = new TargetWebSocketHandler(clientSession);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

            String ip = getAdres(clientSession);

            client.doHandshake(targetHandler, headers, URI.create("wss://localhost:8083/console"));

            clientSession.sendMessage(new TextMessage("Удачное подключение"));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                clientSession.sendMessage(new TextMessage("Ошибка подключения: " + e.getMessage()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Отправка сообщения: " + message);
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
                    System.out.println("Отправка на клиент: " + message);
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