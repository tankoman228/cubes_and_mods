package com.cubes_and_mods.web.Clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.DB.Tariff;

import reactor.core.publisher.Mono;

@Service
public class RootClient {
    private WebClient webClient;

    public RootClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8083")
        		.build();
    }
    
    public ResponseEntity<Void> Launch(int id) {
        try {
            Mono<ResponseEntity<Void>> responseMono = webClient.post()
                .uri("/launch")
                .bodyValue(id)
                .retrieve()
                .toEntity(Void.class);

            ResponseEntity<Void> response = responseMono.block();

            if (response != null) {
                return response;
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch(WebClientResponseException e) { 
        	e.printStackTrace();
        	System.out.println("ID сервера: "+id);
            return new ResponseEntity<>(e.getStatusCode());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public Mono<ResponseEntity<Boolean>> isAlive(int id){
        return webClient.post()
                .uri("/is_alive")
                .bodyValue(id)
                .retrieve()
                .toEntity(Boolean.class)
                .onErrorResume(e -> {
                    return Mono.just(new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }
}
