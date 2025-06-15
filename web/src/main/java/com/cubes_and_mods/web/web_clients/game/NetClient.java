package com.cubes_and_mods.web.web_clients.game;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class NetClient {

    private WebClient webClient;

    @Value("${host-address}")
	private String MainUri;

    @PostConstruct
    private void INIT() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("host"))
                    .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                    .build())
        		.build();     
    }


    public Mono<ResponseEntity<String>> netConfig(int id, String token) {
        return webClient.post()
                        .uri("/global_network_config/" + id)
                        .bodyValue(new ProtectedRequest<Void>(null, token))
                        .retrieve()
                        .toEntity(String.class)
                        .onErrorResume(e -> ErrorHandler.handleError(e));
    }
}
