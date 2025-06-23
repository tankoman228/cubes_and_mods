package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cubes_and_mods.web.jpa.Client;
import com.cubes_and_mods.web.security.ClientSession;
import com.cubes_and_mods.web.Services.EmailSender;
import com.cubes_and_mods.web.web_clients.MailClient;
import com.cubes_and_mods.web.web_clients.UserClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionListener;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserClient userClient;
	
	@Autowired
    MailClient mailClient;
	
    @Autowired
	EmailSender emailSender;
    
    //@Autowired
    //ProxyConfig ProxyConfig;
    @Value("${local-address}")
    private String MainUri;
	
	@PostMapping("/auth")
	public Mono<ResponseEntity<String>> auth(@RequestBody Client user, HttpSession session) {
		
		System.out.println("Начата обработка входа");
        //String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$"; //пока не убирать
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
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
                    String token = response.getBody();
                    session.setAttribute("email", token);

                    return userClient.get(response.getBody()).flatMap(userB -> {
				
                        ClientSession clientSession = userB.getBody();
                        if(clientSession == null){
                            System.err.println("Сессия не найдена");
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сессия не найдена."));
                        }
                        session.setAttribute("id", clientSession.client.getId());
                        System.out.println("Успешно! Token = " + token);
                        return Mono.just(ResponseEntity.ok("Успешно!"));
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
                    System.err.println("Ошибка: " + error.getMessage());
                    
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(error.getMessage()));
                });
	}
	
	@PostMapping("/register")
	public Mono<ResponseEntity<Boolean>> SignUp(@RequestBody Client user, HttpSession session){

        //String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$"; пока не убирать
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            		.body(false));
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            return Mono.just(ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
            		.body(false));
        }
        
        return userClient.register(user)
        	    .map(response -> {
                    String code = response.getBody();
                    String link = MainUri + "/checkCode?code=" + code;
                    String message = "<h1>Вход в аккаунт Cubes&Mods</h1>"
                            + "<p>Перейдите по ссылке для подтверждения входа: </p>"
                            + "<a href = " + link + ">Нажмите здесь</a>"
                            + "<p>Если это не вы, то просто проигнорируйте это письмо.</p>";
                                
                    emailSender.sendSimpleEmail(user.getEmail(), "Код доступа", message, true);
        	    	return ResponseEntity.status(response.getStatusCode()).body(true);
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
            String token = session.getAttribute("email").toString();
            userClient.logOut(token);
            session.invalidate();
        }
        return Mono.just("index");
    }

    @GetMapping("/confirm")
    public Mono<ResponseEntity<String>> confirm(@RequestParam String code) {
        return userClient.checkCode(code);
    }

    @PutMapping("/changePassword")
    public Mono<ResponseEntity<String>> getMethodName(@RequestBody Client user) {
        return userClient.changePassword(user)
            .map(response -> {
                String code = response.getBody();
                if(code == null){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка при получении кода");
                }
                String link = MainUri + "/checkingPassword?code=" + code;
                String message = "<h1>Смена пароля Cubes&Mods</h1>"
                        + "<p>Перейдите по ссылке для подтверждения смены пароля: </p>"
                        + "<a href = " + link + ">Нажмите здесь</a>"
                        + "<p>Если это не вы, то просто проигнорируйте это письмо.</p>";
                            
                emailSender.sendSimpleEmail(user.getEmail(), "Код доступа", message, true);
                System.out.println(response.getStatusCode().toString());
                return ResponseEntity.status(response.getStatusCode()).body("Успешно!");
            })
            .onErrorResume(e -> {
                System.err.println("Error occurred: " + e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
            });
    }   
}
