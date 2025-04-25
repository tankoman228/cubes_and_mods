package com.cubes_and_mods.order.security;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SecureWebClient {

    private WebClient webClient;


    public <T> void sendRequest(String url, ProtectedRequest body) {
        
    }
}
