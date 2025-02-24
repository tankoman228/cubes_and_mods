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

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
    	
        RegisterMsRequest request = new RegisterMsRequest("order", "8082");
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
	

    public final HashMap<String, X509Certificate> certByType = new HashMap<>();

    @PostConstruct
    private void init() {
        try {
            System.out.println("ServiceMicroservices initializing");

            // Загружаем truststore
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (InputStream is = new ClassPathResource("clientTrustStore.jks").getInputStream()) {
                trustStore.load(is, "yourpassword".toCharArray());
            }
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

            Enumeration<String> aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = trustStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    X509Certificate x509 = (X509Certificate) cert;
                    String subjectDN = x509.getSubjectX500Principal().getName();
                    String commonName = extractCN(subjectDN);
                    System.out.println("Загружен сертификат для службы: " + commonName);
                    certByType.put(commonName, x509);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Извлечение CN из DN с использованием LdapName для надёжного парсинга
    private String extractCN(String dn) throws Exception {
        for (var rdn : new javax.naming.ldap.LdapName(dn).getRdns()) {
            if ("CN".equalsIgnoreCase(rdn.getType())) {
                return rdn.getValue().toString();
            }
        }
        return null;
    }
}