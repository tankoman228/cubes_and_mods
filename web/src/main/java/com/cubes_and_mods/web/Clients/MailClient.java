package com.cubes_and_mods.web.Clients;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MailClient {
	
    private WebClient webClient;

    public MailClient() {
        this.webClient = WebClient.builder()
        		.baseUrl("http://localhost:8085/users")
        		.build();
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
