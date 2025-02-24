package com.cubes_and_mods.order;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import jakarta.annotation.PostConstruct;
import reactor.netty.http.client.HttpClient;

@Component
public class MicroserviceInitializer {

	@Value("${server.port}")
	private String port; 
	
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
    	
        RegisterMsRequest request = new RegisterMsRequest("order", port);
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
       
        
        WebClient webClient = WebClient.builder()
                .baseUrl("https://127.0.0.1:8085/")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .secure(sslContextSpec -> {
                            try {
                            	 // Загрузка вашего trust store
                                KeyStore trustStore = KeyStore.getInstance("JKS");
                                try (FileInputStream trustStoreStream = new FileInputStream("src/main/resources/clientTrustStoreauth.jks")) {
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


        
        webClient.put()
            .uri("/microservice/register")
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(response -> System.out.println("Регистрация успешна: " + response.getStatusCode()))
            .doOnError(error -> System.err.println("Ошибка регистрации: " + error.getMessage()))
            .subscribe();
    }
    
	public class RegisterMsRequest {
		public RegisterMsRequest(String string, String string2) {
			this.ms_type = string;
			port = string2;
		}
		public String ms_type;
		public String port;
	}
}