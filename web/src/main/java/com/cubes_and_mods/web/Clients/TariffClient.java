package com.cubes_and_mods.web.Clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TariffClient {

	/*@Value("${services.res.uri}")
	private String MainUri;*/
	
	private String MainUri = "http://localhost:8089/buy";
	
    private WebClient webClient;

    public TariffClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri + "/tariffs")
        		.build();
    }
    
    public Flux<Tariff> getAllTariffs() {
        return webClient.get()
                .uri("")
                .retrieve()
                .bodyToFlux(Tariff.class)
                .onErrorResume(e -> {
                	System.err.println(e.getMessage());
	                return Flux.error(new ResponseStatusException(
	                		HttpStatus.INTERNAL_SERVER_ERROR, 
	                		"Ошибка при получении тарифов"));
                });
    }
    
    public Mono<ResponseEntity<Tariff>> getTariffById(int id) {
        return webClient.get()
                .uri("/" + id)
                .retrieve()
                .toEntity(Tariff.class)
                .onErrorResume(e -> {
                	return Mono.error(new ResponseStatusException(
                			HttpStatus.INTERNAL_SERVER_ERROR,
                			"Ошибка при получении данных о тарифе"));
                });
    }
    
    public Mono<ResponseEntity<Tariff>> createTariff(Tariff tariff){
    	return webClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .toEntity(Tariff.class);
    }
    
    public Mono<ResponseEntity<Tariff>> updateTariff(int id, Tariff tariff){
    	return webClient.put()
			.uri("/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .toEntity(Tariff.class);
    }
    
    public Mono<ResponseEntity<Void>> deleteTariff(int id){
    	return webClient.delete()
			.uri("/" + id)
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                System.err.println("Error occurred: " + e.getMessage());
                return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            });
    }
}
