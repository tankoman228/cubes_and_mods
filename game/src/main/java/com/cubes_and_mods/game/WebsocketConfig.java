package com.cubes_and_mods.game;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.cubes_and_mods.game.controller.WebsocketFile;
import com.cubes_and_mods.game.controller.WebsocketMinecraftConsole;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebsocketMinecraftConsole(), "/console").setAllowedOrigins("*");
        registry.addHandler(new WebsocketFile(), "/file").setAllowedOrigins("*");
    }
}