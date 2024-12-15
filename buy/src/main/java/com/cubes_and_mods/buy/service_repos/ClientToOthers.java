package com.cubes_and_mods.buy.service_repos;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cubes_and_mods.buy.ConfigNetwork;
import com.cubes_and_mods.buy.db.Machine;
import com.cubes_and_mods.buy.db.Mineserver;
import com.cubes_and_mods.buy.db.Tariff;

import reactor.core.publisher.Flux;

/**
 * Для общения с другими микросервисами
 * */
@Service
public class ClientToOthers {

	private WebClient webClient;
	
	  public ClientToOthers() {
		  
		  ConfigNetwork.INIT_CONFIG();
		  
	      this.webClient = WebClient.builder()
	        		.baseUrl(ConfigNetwork.GATEWAY)
	        		.build();
	  }
	  
	  boolean TryReserve(Mineserver mineserver, Tariff t) { 
		  
		  var response = webClient
			        .post()
			        .uri("res/reserve/" + mineserver.getIdMachine())
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
			        .uri("res/free/" + id_machine)
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
			        .uri("res/can_handle/" + id_machine + "/" + id_tariff)
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
			        .uri("res/can_update_tariff/" + id_mine + "/" + id_tariff)
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
