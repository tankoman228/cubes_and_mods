package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.Clients.UserClient;
import com.cubes_and_mods.web.DB.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserClient userClient;
	
	@PostMapping("/auth")
    public Mono<ResponseEntity<User>> auth(@RequestBody User user, HttpSession session) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            		.body(null));
        }
		
        if (user.getPassword() == null || user.getPassword().length() < 3) {
        	System.out.println("Короткий пароль " + user.getPassword());
            return Mono.just(ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
            		.body(null));
        }
		
        return userClient.auth(user)
        		.map(response -> {
        			if(response.getStatusCode() == HttpStatus.OK) {
            			User usr = response.getBody();
            			session.setAttribute("id", usr.getId());
            			session.setAttribute("email", usr.getEmail());
            			session.setAttribute("pwd", usr.getPassword());
            	    	return ResponseEntity.status(response.getStatusCode())
            	    			.body(usr);
        			}
					return response;
                })
                .onErrorResume(e -> {
                    System.err.println("Error occurred: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    		.body(null));
                });
	}
	
	@PostMapping("/register")
	public Mono<ResponseEntity<Boolean>> SignUp(@RequestBody User user, HttpSession session){

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            		.body(false));
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            return Mono.just(ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
            		.body(false));
        }
        
        //session.setAttribute("email", user.getEmail());
        
        return userClient.register(user)
        	    .map(response -> {
        	    	return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        	    })
        	    .onErrorResume(e -> {
        	        System.err.println("Error occurred: " + e.getMessage());
        	        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false));
        	    });
	}
	
	@PostMapping("/ban")
	public Mono<ResponseEntity<Boolean>> Ban(){
		return null;
	}
	
	@PostMapping("/forgive")
	public Mono<ResponseEntity<Boolean>> Forgive(){
		return null;
	}
	
    @GetMapping("/logout")
    public Mono<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Mono.just("index");
    }
}
