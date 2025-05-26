package com.cubes_and_mods.host.security;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceClientSession {

    @Value("${auth-address}")
    private String authAddress;

    private final ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();

    public ClientSession getSession(String token) {

        if (token == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (sessions.containsKey(token)) {
            return sessions.get(token);
        }

        WebClient webClient = WebClient.builder()
        .baseUrl(authAddress) 
        .clientConnector(ClientConnectorForKey.getForKey("auth"))
        .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                .build())
        .build();
        
        var request = new ProtectedRequest<String>(token);

        try {
            var r = webClient.post()
            .uri("/users/get_session")
            .bodyValue(request)
            .retrieve()
            .toEntity(ClientSession.class)
            .block();

            if (!r.getStatusCode().isError()) {
                sessions.put(token, r.getBody());
            }

            return r.getBody();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
