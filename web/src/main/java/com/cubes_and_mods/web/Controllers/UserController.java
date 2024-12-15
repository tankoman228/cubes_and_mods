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
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.Clients.UserClient;
import com.cubes_and_mods.web.DB.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserClient userClient;
	
	@PostMapping("/auth")
    public Mono<ResponseEntity<String>> auth(@RequestBody User user, HttpSession session) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body("Введите корректный адрес электронной почты"));
        }
		
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body("Пароль должен быть длинной не менее 3 символов"));
        }
        
        session.setAttribute("email", user.getEmail());
		
        return userClient.auth(user)
        		.flatMap(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body(response.getBody()));
                    } else {
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body("Ошибка при входе: " + response.getBody()));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error occurred: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    		.body(e.getMessage()));
                });
	}
	
	@PostMapping("/register")
	public Mono<ResponseEntity<String>> SignUp(@RequestBody User user, HttpSession session){

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body("Введите корректный адрес электронной почты"));
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body("Пароль должен быть длинной не менее 3 символов"));
        }
        
        session.setAttribute("email", user.getEmail());
        
        return userClient.register(user)
        		.flatMap(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body(response.getBody()));
                    } else {
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body(response.getBody()));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error occurred: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Произошла ошибка при регистрации пользователя: "+e.getMessage()));
                });
	}
	
	@PostMapping("/ban")
	public ResponseEntity<Boolean> Ban(){
		return null;
	}
	
	@PostMapping("/forgive")
	public ResponseEntity<Boolean> Forgive(){
		return null;
	}
	
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "index";
    }
}
