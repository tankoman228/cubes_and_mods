package com.cubes_and_mods.order.service_repos;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.order.ConfigNetwork;
import com.cubes_and_mods.order.db.Mineserver;
import com.cubes_and_mods.order.db.Tariff;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslContextBuilder;



/**
 * Для общения с другими микросервисами. Вызовы API для службы "res"
 * */
@Service
public class ApiClientToRes {

	private WebClient webClient;
	
	  public ApiClientToRes() {
		  
		  ConfigNetwork.INIT_CONFIG(); //Пока тестирую, временно заменил адрес
		  
	      this.webClient = WebClient.builder()
	        		.baseUrl("https://localhost:8084/")	
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
	  }
	  
	  boolean TryReserve(Mineserver mineserver, Tariff t) { 
		  
		  var response = webClient
			        .post()
			        .uri("machines/reserve/" + mineserver.getIdMachine())
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(t)
			        .retrieve()
			        .toEntity(Void.class) 
			        .block();

			    if (response != null) {
			    	
			        var statusCode = response.getStatusCode();
			        if (statusCode == HttpStatus.OK) {
			            System.out.println("Резервирование прошло успешно!");	
			            return true;
			        } else if (statusCode == HttpStatus.I_AM_A_TEAPOT) {
			            System.out.println("Не удалось выполнить резервирование: I'm a teapot!");
			        } else {
			            System.out.println("Получен статус: " + statusCode);
			        }
			    } else {
			        System.out.println("Ответ не получен.");
			    }
		  
		  return false;
	  }
	  
	  public void free(int id_machine, Tariff t) {
		  
		  var response = webClient
			        .post()
			        .uri("machines/free/" + id_machine)
			        .contentType(MediaType.APPLICATION_JSON)
			        .bodyValue(t)
			        .retrieve()
			        .toEntity(Void.class) 
			        .block();

			    if (response != null) {
			    	
			        var statusCode = response.getStatusCode();
			        if (statusCode == HttpStatus.OK) {
			            System.out.println("Успешно!");			        
			        } else {
			            System.out.println("Получен статус: " + statusCode);
			        }
			    } else {
			        System.out.println("Ответ не получен.");
			    }
	  }
	  
	  public boolean can_handle(int id_machine, int id_tariff) {
		  
		  var response = webClient
			        .post()
			        .uri("machines/can_handle/" + id_machine + "/" + id_tariff)
			        .retrieve()
			        .toEntity(Boolean.class) 
			        .block();

			    if (response != null) {
			    	
			        var statusCode = response.getStatusCode();
			        if (statusCode == HttpStatus.OK) {
			        	return response.getBody();
			        }
			    } else {
			        System.out.println("Ответ не получен.");
			    }	  
		  return false;
	  }
	  
	  public boolean can_update_tariff(int id_mine, int id_tariff) {
		  
		  var response = webClient
			        .post()
			        .uri("machines/can_update_tariff/" + id_mine + "/" + id_tariff)
			        .retrieve()
			        .toEntity(Boolean.class) 
			        .block();

			    if (response != null) {
			    	
			        var statusCode = response.getStatusCode();
			        if (statusCode == HttpStatus.OK) {
			        	return response.getBody();
			        }
			    } else {
			        System.out.println("Ответ не получен.");
			    }	  
		  return false;
	  }
}
