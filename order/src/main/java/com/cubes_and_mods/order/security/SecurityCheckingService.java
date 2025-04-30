package com.cubes_and_mods.order.security;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

@Service
public class SecurityCheckingService {

    private static final HashMap<String, SecurityChecker> securityCheckers = new HashMap<>();

    public void addForEndpoint(String endpoint, SecurityChecker securityChecker) {
        securityCheckers.put(endpoint, securityChecker);
    }

    public boolean checkRequest(String endpoint, ProtectedRequest<?> request) {

        var checker = securityCheckers.get(endpoint);
        if (checker == null) {
            return true; // Разрешим, если запрос игнорируется
        }

        var webClient = WebClient.builder()
            .baseUrl("https://localhost:8085/")	//TODO: заменить на реальный адрес
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

            for (var allowed : checker.allowedOrigins.value()) {
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
