package com.cubes_and_mods.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.cubes_and_mods.game.controller.WebsocketMinecraftConsole;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebsocketMinecraftConsole websocketMinecraftConsole; // Внедряем WebsocketMinecraftConsole

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketMinecraftConsole, "/console").setAllowedOrigins("*");
    }
}