package com.cubes_and_mods.auth.service;


import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.auth.security.ClientConnectorForKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Верификация типа службы по её ключу, если не получится отправить запрос с валидным сертификатом, то сервису откажет
 */
public class VerifyWebClient {

	private WebClient webClient;

    public VerifyWebClient(String ip_port, String stype) {
    	
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        
        webClient = WebClient.builder()
                .baseUrl("https://" + ip_port.replace("127.0.0.1", "localhost") + "/") //ХЗ, но 127.0.0.1 всё ломает
                .clientConnector(ClientConnectorForKey.getForKey(stype))
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
