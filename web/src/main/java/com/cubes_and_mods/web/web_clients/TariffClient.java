package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.DB.Tariff;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TariffClient {
	
    @Autowired
    ProxyConfig ProxyConfig;
	
	private String MainUri;
	
	/*@Value("${services.buy.uri}")
	String MainUri;*/
	
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	
    	MainUri = ProxyConfig.getBuy() + "/tariffs";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    public Flux<Tariff> getAllTariffs() {
    	System.err.println(MainUri);
        return webClient.get()
                .retrieve()
                .bodyToFlux(Tariff.class)
                .onErrorResume(e -> {
                	System.err.println(e.getMessage());
	                return ErrorHandler.handleErrorFlux(e);
                });
    }
    
    public Mono<ResponseEntity<Tariff>> getTariffById(int id) {
        return webClient.get()
                .uri("/" + id)
                .retrieve()
                .toEntity(Tariff.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleError(e);
                });
    }
    
    public Mono<ResponseEntity<Tariff>> createTariff(Tariff tariff){
    	return webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .toEntity(Tariff.class)
            .onErrorResume(e -> {
                return ErrorHandler.handleError(e);
            });
    }
    
    public Mono<ResponseEntity<Tariff>> updateTariff(int id, Tariff tariff){
    	return webClient.put()
			.uri("/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .toEntity(Tariff.class)
            .onErrorResume(e -> {
                return ErrorHandler.handleError(e);
            });
    }
    
    public Mono<ResponseEntity<Void>> deleteTariff(int id){
    	return webClient.delete()
			.uri("/" + id)
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                System.err.println("Error occurred: " + e.getMessage());
                return ErrorHandler.handleError(e);
            });
    }
}
