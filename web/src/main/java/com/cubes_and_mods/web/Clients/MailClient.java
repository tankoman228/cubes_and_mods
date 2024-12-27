package com.cubes_and_mods.web.Clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MailClient {
	
	/*@Value("${services.usr.uri}")
	private String MainUri;*/
	
	private String MainUri = "http://localhost:8089/usr";

	
    private WebClient webClient;

    public MailClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri + "/users")
        		.build();
    }

    public Mono<String> generateCode(String email) {
        return webClient.get()
                .uri("/generate_code?email={email}", email)
                .retrieve()
                .bodyToMono(String.class);
    }
    
    public Mono<ResponseEntity<String>> checkCode(String code) {
        return webClient.get()
                .uri("/check_code?code={code}", code)
                .retrieve()
                .toEntity(String.class);
    }
}
