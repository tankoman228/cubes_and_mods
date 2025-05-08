package com.cubes_and_mods.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.cubes_and_mods.host.controller.WebsocketMinecraftConsole;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebsocketMinecraftConsole websocketMinecraftConsole; // Внедряем WebsocketMinecraftConsole

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        System.out.println("WebsocketConfig.registerWebSocketHandlers <------------------------------ bibki");
        // I've seen this print

        registry.addHandler(websocketMinecraftConsole, "/console")
        .setAllowedOrigins("*");
    }
}