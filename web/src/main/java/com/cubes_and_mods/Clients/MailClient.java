package com.cubes_and_mods.Clients;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class MailClient {
	
    private WebClient webClient;

    public MailClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085").build(); // Замените на ваш URL
    }

    public Mono<String> generateCode(String email) {
        return webClient.get()
                .uri("/generate_code?email={email}", email)
                .retrieve()
                .bodyToMono(String.class);
    }
    
    public Mono<String> checkCode(String code) {
        return webClient.get()
                .uri("//check_code?code={code}", code)
                .retrieve()
                .bodyToMono(String.class);
    }
}
