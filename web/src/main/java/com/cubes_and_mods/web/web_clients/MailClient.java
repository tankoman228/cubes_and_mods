package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class MailClient {
	
    @Value("${auth-address}")
	private String MainUri;

    private WebClient webClient;

    @PostConstruct
    private void INIT() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri + "/users")
        		.build();
    }

    public Mono<ResponseEntity<String>> generateCode(String email) {
        return webClient.get()
                .uri("/generate_code?email={email}", email)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleErrorString(e);
                });
    }
    
    public Mono<ResponseEntity<String>> checkCode(String code) {
        return webClient.get()
                .uri("/check_code?code={code}", code)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleErrorString(e);
                });
    }
}
