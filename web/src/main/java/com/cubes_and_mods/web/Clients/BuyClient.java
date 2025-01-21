package com.cubes_and_mods.web.Clients;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.model.ORDER_REQUEST;
import com.cubes_and_mods.web.Clients.model.Order;

import reactor.core.publisher.Mono;

@Service
public class BuyClient {
	
	/*@Value("${services.buy.uri}")
	private String MainUri;*/
	
	private String MainUri = ProxyConfig.getBuy() + "/pay";
			
    private WebClient webClient;

    public BuyClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
        		.build();
    }
    
    private <T> Mono<ResponseEntity<T>> makeRequest(String uri, Object body, Class<T> responseType) {
        return webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .toEntity(responseType)
                .onErrorResume(e -> ErrorHandler.handleError(e));
    }

    public Mono<ResponseEntity<String>> request(ORDER_REQUEST body) {
        //return makeRequest("/make_order", body, String.class);
        return webClient.post()
                .uri("/make_order")
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> ErrorHandler.handleErrorString(e));
    }

    public Mono<ResponseEntity<Void>> confirm(String key) {
        return makeRequest("/confirm", key, Void.class);
    }

    public Mono<ResponseEntity<Void>> decline(String key) {
        return makeRequest("/decline", key, Void.class);
    }

    public Mono<ResponseEntity<Order>> status(String key) {
        return makeRequest("/status", key, Order.class);
    }

    public Mono<ResponseEntity<Void>> returnBuy() {
        return makeRequest("/return_money", null, Void.class);
    }
}
