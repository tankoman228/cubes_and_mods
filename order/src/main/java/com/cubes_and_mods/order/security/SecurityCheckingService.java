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

    public boolean checkRequest(String endpoint, String alpha) {

        var webClient = WebClient.builder()
            .baseUrl("https://localhost:8085/")	//TODO: заменить на реальный адрес
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                    .secure(sslContextSpec -> {
                        try {
                            // Загрузка вашего trust store
                            KeyStore trustStore = KeyStore.getInstance("JKS");
                            try (FileInputStream trustStoreStream = new FileInputStream("src/main/resources/clientTrustStore.jks")) {
                                trustStore.load(trustStoreStream, "yourpassword".toCharArray());
                            }

                            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                            trustManagerFactory.init(trustStore);

                            sslContextSpec.sslContext(SslContextBuilder.forClient()
                                    .trustManager(trustManagerFactory));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to set SSL context", e);
                        }
                    }))
                )
            .build();

        webClient.

        return false;
    }
}
