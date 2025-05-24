package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.dto.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TariffClient {
	
    @Autowired
    ProxyConfig ProxyConfig;
	
	@Value("${order-address}")
	private String MainUri;
	
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	
    	MainUri += "available";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("order"))
        		.build();
    }
    
    public Flux<Tariff> getAllTariffs(String token) {
        System.out.println(MainUri + "/tariffs");
        return webClient.post()
                .uri("/tariffs")
                .bodyValue(new ProtectedRequest<Void>(null, token))
                .retrieve()
                .bodyToFlux(Tariff.class)
                .onErrorResume(e -> {
                	System.err.println(e.getMessage());
	                return ErrorHandler.handleErrorFlux(e);
                });
    }
    
    public Flux<Server> getServersForTariffs(int id, String token) {
        return webClient.post()
                .uri("/servers_for_tariff/" + id)
                .bodyValue(new ProtectedRequest<Void>(null, token))
                .retrieve()
                .bodyToFlux(Server.class)
                .onErrorResume(e -> {
                	System.err.println(e.getMessage());
	                return ErrorHandler.handleErrorFlux(e);
                });
    }
    

    public Mono<ResponseEntity<Tariff>> getTariffById(int id, String token) {
        return webClient.post()
                .uri("/tariff/" + id)
                .bodyValue(new ProtectedRequest<Void>(null, token))
                .retrieve()
                .toEntity(Tariff.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
}
