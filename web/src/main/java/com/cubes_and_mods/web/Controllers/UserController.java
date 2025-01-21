package com.cubes_and_mods.web.Controllers;

import java.util.HashMap;
import java.util.Map;

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

import com.cubes_and_mods.web.ProxyConfig;
import com.cubes_and_mods.web.Clients.MailClient;
import com.cubes_and_mods.web.Clients.UserClient;
import com.cubes_and_mods.web.DB.User;
import com.cubes_and_mods.web.Services.EmailSender;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserClient userClient;
	
	@Autowired
    MailClient mailClient;
	
    @Autowired
	EmailSender emailSender;
	
	@PostMapping("/auth")
	public Mono<ResponseEntity<String>> auth(@RequestBody User user, HttpSession session) {
		System.out.println("Начата обработка входа");
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
        	System.err.println("Вы ввели невалидную почту");
            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            		.body("Вы ввели невалидную почту"));
        }
        System.out.println("Почта прошла");
        if (user.getPassword() == null || user.getPassword().length() < 3) {
        	System.err.println("Короткий пароль " + user.getPassword());
            return Mono.just(ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
            		.body("Пароль должен быть длинной не менее 3 символов"));
        }
        System.out.println("Пароль прошел");
        return userClient.auth(user)
                .flatMap(response -> {
                    User usr = response.getBody();

                    if(usr == null) {
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body("Неверный логин или пароль"));
                    }
                    
                    return mailClient.generateCode(usr.getEmail())
                            .flatMap(responseMail -> {
                            	System.out.println("Данные валидны, начало отправки сообщения");
                                String link = ProxyConfig.getLocal() + "/checkCode?code=" + responseMail.getBody();
                                String message = "<h1>Вход в аккаунт Cubes&Mods</h1>"
                                		+ "<p>Перейдите по ссылке для подтверждения входа: </p>"
                                		+ "<a href = " + link + ">Нажмите здесь</a>"
                                		+ "<p>Если это не вы, то просто проигнорируйте это письмо.</p>";
                                
                                System.out.println(link);
                                emailSender.sendSimpleEmail(usr.getEmail(), "Код доступа", message, true);
                                RequestKeeper.authRequests.put(responseMail.getBody(), usr);
                                return Mono.just(ResponseEntity.ok("Вам на почту отправлен код подтверждения"));
                            })
                            .onErrorResume(errorMail -> {
                                if (errorMail instanceof MailException) {
                                    MailException mailEx = (MailException) errorMail;
                                    System.out.println(mailEx.getMessage());
                                    if (mailEx.getMessage().contains("550")) {
                                    	System.err.println("Неверный адрес получателя");
                                        return Mono.just(ResponseEntity.badRequest()
                                                .body("Неверный адрес получателя: " + usr.getEmail()));
                                    }
                                    System.err.println("Не удается отправить сообщение: " + mailEx.getMessage());
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .body("Не удается отправить сообщение: " + mailEx.getMessage()));
                                } else {
                                	System.err.println("Произошла ошибка: " + errorMail.getMessage());
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .body("Произошла ошибка: " + errorMail.getMessage()));
                                }
                            });
                })
                .onErrorResume(error -> {
                    if (error.getMessage().contains("404")) {
                    	System.err.println("404");
                    	return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Пользователь не найден."));
                    }
                    if (error.getMessage().contains("400")) {
                    	System.err.println("400");
                    	return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Пользователь заблокирован или неверные данные."));
                    }
                    if (error.getMessage().contains("403")) {
                    	System.err.println("403");
                    	return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Неверный пароль."));
                    }
                    System.err.println(error.getMessage());
                    
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(error.getMessage()));
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
	
    @GetMapping("/logout")
    public Mono<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Mono.just("index");
    }
}
