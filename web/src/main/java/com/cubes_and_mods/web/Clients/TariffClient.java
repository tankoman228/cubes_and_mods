package com.cubes_and_mods.web.Clients;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TariffClient {

    private WebClient webClient;

    public TariffClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8082/tariffs")
        		.build();
    }
    
    public Flux<Tariff> getAllTariffs() {
        return webClient.get()
                .uri("/tariffs")
                .retrieve()
                .bodyToFlux(Tariff.class);
    }
    
    public Mono<Tariff> getTariffById(int id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> createTariff(Tariff tariff){
    	return webClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<Tariff> updateTariff(int id, Tariff tariff){
    	return webClient.put()
            .uri("/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(tariff)
            .retrieve()
            .bodyToMono(Tariff.class);
    }
    
    public Mono<ResponseEntity<Void>> deleteTariff(int id){
    	return webClient.delete()
            .uri("/{id}", id)
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                System.err.println("Error occurred: " + e.getMessage());
                return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            });
    }
}
