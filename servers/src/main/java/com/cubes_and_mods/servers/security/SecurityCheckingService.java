package com.cubes_and_mods.servers.security;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;

@Service
public class SecurityCheckingService {

    @Value("${auth-address}")
    private String authAddress;

    public boolean checkRequest(AllowedOrigins allowedOrigins, ProtectedRequest<?> request) {

        var webClient = WebClient.builder()
            .baseUrl(authAddress)
            .clientConnector(ClientConnectorForKey.getForKey("auth"))
            .build();
        
        try {
            var r = webClient.post()
                .uri("/microservice/service_type_check")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .doOnError(error -> System.err.println("Ошибка проверки: " + error.getMessage()))
                .block();
        
            var mtype = r.getBody();

            for (var allowed : allowedOrigins.value()) {
                if (allowed.toString().equals(mtype)) {
                    return true;
                }
            }

            System.out.println("Неверный тип сервиса: " + mtype);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
