package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.Clients.model.VersionWithoutArchive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VersionsClient {
	private String MainUri = "http://localhost:8089/res/version";
	
    private WebClient webClient;

    public VersionsClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<VersionWithoutArchive> getAll(){
    	return webClient.get()
    			.uri("/all")
	            .retrieve()
	            .bodyToFlux(VersionWithoutArchive.class)
	            .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
                        return Flux.error(new ResponseStatusException(statusCode, e.getMessage()));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        System.err.println("Error occurred: " + e.getStackTrace());
                        return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                    }
	            });
    }
}
