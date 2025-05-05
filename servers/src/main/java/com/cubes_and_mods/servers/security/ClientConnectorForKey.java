package com.cubes_and_mods.servers.security;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

public class ClientConnectorForKey {

    public static ReactorClientHttpConnector getForKey(String key) {

        return new ReactorClientHttpConnector(HttpClient.create()
        .secure(sslContextSpec -> {
            try {
                // Загрузка вашего trust store
                KeyStore trustStore = KeyStore.getInstance("JKS");
                try (FileInputStream trustStoreStream = new FileInputStream("src/main/resources/clientTrustStore" + key + ".jks")) {
                    trustStore.load(trustStoreStream, "yourpassword".toCharArray());
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                sslContextSpec.sslContext(SslContextBuilder.forClient()
                        .trustManager(trustManagerFactory));
            } catch (Exception e) {
                throw new RuntimeException("Failed to set SSL context", e);
            }
        }));
    }
}
