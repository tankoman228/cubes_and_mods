package com.cubes_and_mods.host.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MicroserviceInitializer {

	@Value("${server.port}")
	private String port; 

    @Value("${auth-address}")
    private String authAddress;

    
    @Autowired
    private SecurityCheckingService securityCheckingService;
	
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
    	
        RegisterMsRequest request = new RegisterMsRequest("host", port);
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
       
        WebClient webClient = WebClient.builder()
                .baseUrl(authAddress) 
                .clientConnector(ClientConnectorForKey.getForKey("auth"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        
        webClient.put()
            .uri("/microservice/register")
            .bodyValue(request)
            .retrieve()
            .toEntity(String.class)
            .doOnSuccess(response -> 
            {
                System.out.println("Регистрация успешна: " + response.getStatusCode());
                ProtectedRequest.serviceSessionIdGlobal = response.getBody();
            })
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