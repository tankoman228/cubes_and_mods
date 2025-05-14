package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ProtectedRequest;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class BuyClient {
	
	@Value("${order-address}")
	private String MainUri;
				
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	
        MainUri += "/orders";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("order"))
        		.build();     
    }

    public Mono<ResponseEntity<String>> request(Order order) {
        //return makeRequest("/make_order", body, String.class);
        return webClient.post()
                .uri("/make_order")
                .bodyValue(new ProtectedRequest<Order>(order))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> ErrorHandler.handleErrorString(e));
    }

    public Mono<ResponseEntity<Void>> confirm(String key) {
        return webClient.put()
            .uri("/confirm/"+key)
            .bodyValue(new ProtectedRequest<Void>())
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
            });
    }

    public Mono<ResponseEntity<Void>> decline(String key) {
        return webClient.put()
            .uri("/cancel/"+key)
            .bodyValue(new ProtectedRequest<Void>())
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
            });
    }

    public Mono<ResponseEntity<Order>> status(String key) {
        return webClient.post()
            .uri("/confirm/"+key)
            .bodyValue(new ProtectedRequest<Void>())
            .retrieve()
            .toEntity(Order.class)
            .onErrorResume(e -> ErrorHandler.handleError(e));
    }

    //TODO: По идее не нужен клиенту, подумать
    public Mono<ResponseEntity<Void>> statuses() {
        return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
    }
}
