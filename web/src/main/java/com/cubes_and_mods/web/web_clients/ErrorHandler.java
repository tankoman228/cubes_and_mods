package com.cubes_and_mods.web.web_clients;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Для отлова в вебклиентах ошыбок, логирование
 * */
public class ErrorHandler {
	
    public static <T> Mono<ResponseEntity<T>> handleError(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(null));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
        }
    }
    
    public static Mono<ResponseEntity<String>> handleErrorString(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(e.getMessage()));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }
    }
    
    public static Mono<ResponseEntity<Integer>> handleErrorInt(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(-1));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1));
        }
    }
    
    public static Mono<ResponseEntity<Boolean>> handleErrorBool(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatus statusCode = (HttpStatus) webClientResponseException.getStatusCode();
            return Mono.just(ResponseEntity.status(statusCode).body(false));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Error occurred: " + e.getStackTrace());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false));
        }
    }
    
    public static <T> Flux<T> handleErrorFlux(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientResponseException = (WebClientResponseException) e;
            HttpStatusCode statusCode = webClientResponseException.getStatusCode();

            return Flux.error(new ResponseStatusException(statusCode, e.getMessage()));
        } else {
            System.err.println("Error occurred: " + e.getMessage());
            return Flux.error(new ResponseStatusException(
            		HttpStatus.INTERNAL_SERVER_ERROR, 
            		"Непредвиденная ошибка на сервере"));
        }
    }
}
