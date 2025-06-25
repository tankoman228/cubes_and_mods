package com.cubes_and_mods.web.web_clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.web.jpa.*;
import com.cubes_and_mods.web.security.ClientConnectorForKey;
import com.cubes_and_mods.web.security.ClientSession;
import com.cubes_and_mods.web.security.ProtectedRequest;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

	@Value("${auth-address}")
	private String MainUri;
	
    //@Autowired
    //ProxyConfig ProxyConfig;
	
    private WebClient webClient;

    @PostConstruct
    private void INIT() {
    	MainUri += "users";
    	
        this.webClient = WebClient.builder()
        		.baseUrl(MainUri)
                .clientConnector(ClientConnectorForKey.getForKey("auth"))
        		.build();
    }

    public Mono<ResponseEntity<String>> auth(Client user) {
    	System.out.println(user.getEmail()+" "+user.getPassword()+" "+user.getBanned());
        return webClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProtectedRequest<Client>(user))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    System.err.println("Ошибка запроса на вход");
                    return ErrorHandler.handleError(e);
                });
    }

    
    public Mono<ResponseEntity<String>> register(Client user) {
        return webClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProtectedRequest<Client>(user))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }

    public Mono<ResponseEntity<String>> checkCode(String code) {
        return webClient.post()
                .uri("/confirm")
                .bodyValue(new ProtectedRequest<String>(code))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                	return ErrorHandler.handleErrorString(e);
                });
    }
    
    public Mono<ResponseEntity<String>> changePassword(Client user) {
        return webClient.put()
                .uri("/change_password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProtectedRequest<Client>(user))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> {
                    return ErrorHandler.handleError(e);
                });
    }

    public Mono<ResponseEntity<Void>> logOut(String token) {
        return webClient.post()
                .uri("/logout")
                .bodyValue(new ProtectedRequest<String>(token, token))
                .retrieve()
                .toEntity(Void.class)
                .onErrorResume(e -> {
                	return Mono.just(new ResponseEntity<Void>(HttpStatusCode.valueOf(500)));
                });
    }
    
    public Mono<ResponseEntity<ClientSession>> get(String token) {
        return webClient.post()        		
                .uri("/get_session")
                .bodyValue(new ProtectedRequest<String>(token))
                .retrieve()                
                .toEntity(ClientSession.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
                        HttpStatusCode statusCode = webClientResponseException.getStatusCode();

                        System.err.println(statusCode.toString());
                        if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                        	System.err.println("Сессия не найдена");
                        }
                        else {
                        	System.err.println("Гачимучи");
                        }
                    } else {
                        System.err.println("Error occurred: " + e.getMessage());
                    }
                    return null;
                });
    }
}
