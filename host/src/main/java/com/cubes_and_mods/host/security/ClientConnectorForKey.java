package com.cubes_and_mods.host.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
                try (InputStream trustStoreStream = ClientConnectorForKey.class.getClassLoader()
                        .getResourceAsStream("clientTrustStore" + key + ".jks")) {
                    if (trustStoreStream == null) {
                        throw new FileNotFoundException("Truststore not found in resources");
                    }
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
