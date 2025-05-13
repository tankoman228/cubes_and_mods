package com.cubes_and_mods.web.web_clients.res;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.web_clients.ErrorHandler;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

@Service
public class VersionsClient {
	
    @Value("${servers-address}")
	private String MainUri;
	
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	
    	MainUri += "/games";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("servers"))
        		.build();
    }
    
    public Flux<Game> getAllGames(){
    	return webClient.post()
    			.uri("")
                .bodyValue(new ProtectedRequest<Void>())
	            .retrieve()
	            .bodyToFlux(Game.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }

    public Flux<Version> getVersionByGameID(int id){
    	return webClient.post()
    			.uri("/{id}/versions", id)
                .bodyValue(new ProtectedRequest<Void>())
	            .retrieve()
	            .bodyToFlux(Version.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }

    public Flux<Version> searchVersionByGameID(int id, String search){
    	return webClient.post()
    			.uri("/{id}/versions/search", id)
                .bodyValue(new ProtectedRequest<String>(search))
	            .retrieve()
	            .bodyToFlux(Version.class)
	            .onErrorResume(e -> {
                    return ErrorHandler.handleErrorFlux(e);
	            });
    }
}
