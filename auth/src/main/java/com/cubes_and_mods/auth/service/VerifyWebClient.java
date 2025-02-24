package com.cubes_and_mods.auth.service;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManagerFactory;


public class VerifyWebClient {

	private WebClient webClient;

    public VerifyWebClient(String ip_port, String stype) {
    	
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        
        webClient = WebClient.builder()
                .baseUrl("https://" + ip_port.replace("127.0.0.1", "localhost") + "/") //ХЗ, но 127.0.0.1 всё ломает
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .secure(sslContextSpec -> {
                            try {
                            	 // Загрузка вашего trust store
                                KeyStore trustStore = KeyStore.getInstance("JKS");
                                try (FileInputStream trustStoreStream = new FileInputStream("src/main/resources/clientTrustStore" + stype + ".jks")) {
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
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
    }

    public boolean verify(VerifyWebRequest challenge) {
        
        try {
            VerifyWebResponce response = webClient.post()
            		.uri("/verify_ssl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(challenge)
                    .accept(MediaType.APPLICATION_JSON)        
                    .retrieve()
                    .bodyToMono(VerifyWebResponce.class)
                    .block();

            return response.c == (challenge.a + challenge.b);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class VerifyWebRequest { 
        public int a, b;
        public VerifyWebRequest(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class VerifyWebResponce {
        public int c;
        @JsonCreator
        public VerifyWebResponce (@JsonProperty("c") int c) {
            this.c = c;
        }
        public VerifyWebResponce () {}
    }
	
}
