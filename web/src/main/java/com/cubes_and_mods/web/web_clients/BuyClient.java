package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.dto.*;
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
    	
        MainUri += "orders";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("order"))
        		.build();     
    }

    public Mono<ResponseEntity<String>> request(Order order, String token) {
        //return makeRequest("/make_order", body, String.class);
        System.out.println(token);
        return webClient.post()
                .uri("/make_order")
                .bodyValue(new ProtectedRequest<Order>(order, token))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                        System.err.println(statusCode.toString());
                        System.err.println(e.getMessage());
                        System.err.println(e.getCause());
                        e.printStackTrace();
                        return Mono.just(ResponseEntity.status(statusCode).body(e.getMessage()));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage()));
                    }
                });
    }

    public Mono<ResponseEntity<Void>> confirm(String key, String token) {
        System.out.println("Ключ на отправку: " + key);
        return webClient.put()
            .uri("/confirm")
            .bodyValue(new ProtectedRequest<String>(key, token))
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                //return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                    HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                    System.err.println(statusCode.toString());
                    System.err.println(e.getMessage());
                    System.err.println(e.getCause());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(statusCode).body(null));
                } else {
                    System.err.println("Error occurred: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(null));
                }
            });
    }

    public Mono<ResponseEntity<Void>> decline(String key, String token) {
        System.out.println("Ключ на отправку: " + key);
        return webClient.put()
            .uri("/cancel")
            .bodyValue(new ProtectedRequest<String>(key, token))
            .retrieve()
            .toEntity(Void.class)
            .onErrorResume(e -> {
                if (e instanceof WebClientResponseException) {
                    WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                    HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                    System.err.println(statusCode.toString());
                    System.err.println(e.getMessage());
                    System.err.println(e.getCause());
                    e.printStackTrace();

                    return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(statusCode.value())));
                } else {
                    System.err.println("Error occurred: " + e.getMessage());
                    System.err.println(e.getCause());
                }

                return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
            });
    }

    public Mono<ResponseEntity<Order>> status(String key, String token) {
        return webClient.post()
            .uri("/confirm/"+key)
            .bodyValue(new ProtectedRequest<Void>(null, token))
            .retrieve()
            .toEntity(Order.class)
            .onErrorResume(e -> ErrorHandler.handleError(e));
    }

    //TODO: По идее не нужен клиенту, подумать
    public Mono<ResponseEntity<Void>> statuses(String token) {
        return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
    }
}
