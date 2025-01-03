package com.cubes_and_mods.web.Clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.DB.User;

import reactor.core.publisher.Mono;

@Service
public class UserClient {

	/*@Value("${services.usr.uri}")
	private String MainUri;*/
	
	private String MainUri = "http://localhost:8089/usr";
	
    private WebClient webClient;

    public UserClient() {
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri + "/users")
        		.build();
    }

    public Mono<ResponseEntity<User>> auth(User user) {
    	System.out.println(MainUri+"/users"+"/auth");
    	System.out.println(user.getEmail()+" "+user.getPassword()+" "+user.getBanned());
    	//user.setId(-1);
        return webClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .toEntity(User.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
                        return Mono.just(ResponseEntity.status(statusCode).body(null));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        System.err.println("Error occurred: " + e.getStackTrace());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                    }
                });
    }

    
    public Mono<ResponseEntity<Boolean>> register(User user) {
        return webClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .toEntity(Boolean.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                        return Mono.just(new ResponseEntity<>(false, statusCode));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Mono.just(new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }
    
    public Mono<ResponseEntity<String>> ban(String email) {
        return webClient.post()
                .uri("/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(email)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();
                        String responseBody = "";
                        if(statusCode == HttpStatus.NOT_FOUND) {
                        	responseBody = "Пользователь с таким EMAIL не найден";
                        	System.err.println("Пользователь с таким email не найден");
                        }
                        else {
                            responseBody = webClientResponseException.getResponseBodyAsString();
                        }
                        return Mono.just(new ResponseEntity<>(responseBody, statusCode));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }
    
    public Mono<ResponseEntity<String>> forgive(String code) {
        return webClient.post()
                .uri("/forgive")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(code)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();
                        String responseBody = "";
                        if(statusCode == HttpStatus.NOT_FOUND) {
                        	responseBody = "Пользователь с таким EMAIL не найден";
                        	System.err.println("Пользователь с таким email не найден");
                        }
                        else {
                            responseBody = webClientResponseException.getResponseBodyAsString();
                        }
                        return Mono.just(new ResponseEntity<>(responseBody, statusCode));
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                        return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }
}
