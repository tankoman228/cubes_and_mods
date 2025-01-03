package com.cubes_and_mods.web.WebSockets;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;

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
        // Подключаемся к целевому серверу
        connectToTargetServer(session);
    }

    private void connectToTargetServer(WebSocketSession clientSession) {
        StandardWebSocketClient client = new StandardWebSocketClient();
        
        // Создаем новый обработчик для целевого сервера
        TargetWebSocketHandler targetHandler = new TargetWebSocketHandler(clientSession);
        
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        
        // Устанавливаем соединение с целевым сервером
        client.doHandshake(targetHandler, headers, URI.create("ws://localhost:8083/console"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Пересылаем сообщение на соответствующий целевой WebSocket сервер
        WebSocketSession targetSession = targetSessions.get(session);
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clientSessions.remove(session);
        // Удаляем соответствующий целевой сервер, если он существует
        WebSocketSession targetSession = targetSessions.remove(session);
        if (targetSession != null) {
            targetSession.close(); // Закрываем целевую сессию, если она открыта
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error: " + exception.getMessage());
        
        // Удаляем сессию клиента
        clientSessions.remove(session);
        
        // Закрываем целевую сессию, если она существует
        WebSocketSession targetSession = targetSessions.remove(session);
        if (targetSession != null) {
            try {
                targetSession.close(); // Закрываем целевую сессию при ошибке
                System.out.println("Closed target session due to transport error.");
            } catch (Exception e) {
                System.err.println("Error closing target session: " + e.getMessage());
            }
        }
        
        // Отправляем сообщение об ошибке клиенту
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("Error occurred: " + exception.getMessage()));
            } catch (Exception e) {
                System.err.println("Error sending message to client: " + e.getMessage());
            }
        }
    }

    // Внутренний класс для обработки сообщений от целевого сервера
    private class TargetWebSocketHandler extends TextWebSocketHandler {
        
        private WebSocketSession clientSession;

        public TargetWebSocketHandler(WebSocketSession clientSession) {
            this.clientSession = clientSession; // Сохраняем ссылку на клиентскую сессию
            // Сохраняем связь между клиентской сессией и целевой сессией
            targetSessions.put(clientSession, null); 
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            // Сохраняем ссылку на целевую сессию
            targetSessions.put(clientSession, session); 
            System.out.println("Connected to target server.");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            // Пересылаем сообщение обратно клиенту
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
            
            // Закрываем клиентскую сессию, если она открыта
            if (clientSession.isOpen()) {
                try {
                    clientSession.close(new CloseStatus(1000, "Target server closed the connection."));
                } catch (Exception e) {
                    System.err.println("Error closing client session: " + e.getMessage());
                }
            }
            
            // Удаляем ссылку на целевую сессию при закрытии
            targetSessions.remove(clientSession); 
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("Transport error from target server: " + exception.getMessage());
            
            // Если клиентская сессия открыта, отправляем сообщение об ошибке
            if (clientSession.isOpen()) {
                try {
                    // Закрываем клиентскую сессию с кодом 1006 (необычное закрытие)
                    clientSession.close(new CloseStatus(1006, "Target server error: " + exception.getMessage()));
                } catch (Exception e) {
                    System.err.println("Error closing client session: " + e.getMessage());
                }
            }
            
            // Удаляем сессию
            clientSessions.remove(clientSession);
            targetSessions.remove(clientSession);
        }
    }
}