package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.web.Clients.MailClient;
import com.cubes_and_mods.web.Services.EmailSender;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/mail")
public class MailController {
	
	@Autowired
    MailClient mailClient;
	
    @Autowired
	EmailSender emailSender;
	
	@GetMapping("/sendCode")
	public ResponseEntity<String> sendCode(Model model, @RequestParam String email) {
		try {			
			mailClient.generateCode(email).subscribe(code -> {
		        emailSender.sendSimpleEmail(email, "Код доступа", "Ваш код доступа: " + code);
		    }, error -> {
		        if (error instanceof MailException) {
		            MailException mailEx = (MailException) error;
		            System.out.println(mailEx.getMessage());
		            if (mailEx.getMessage().contains("550")) {
		                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный адрес получателя: " + email);
		            }
		            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удается отправить сообщение: " + mailEx.getMessage());
		        } else {
		            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка: " + error.getMessage());
		        }
		    });
			return ResponseEntity.ok("Код доступа отправлен на почту");
		}
		catch (ResponseStatusException ex){
			System.out.println(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
		}
	}
	
	@GetMapping("/checkCode")
	public Mono<ResponseEntity<String>> checkCode(Model model, @RequestParam String code) {
		return mailClient.checkCode(code)
		        //.map(email -> ResponseEntity.ok(email))
        		.flatMap(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                    	System.out.println("Удачно: "+response.getStatusCode()+" "+response.getBody());
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body(response.getBody()));
                    } else {
                    	System.err.println(response.getStatusCode()+" "+response.getBody());
                        return Mono.just(ResponseEntity.status(response.getStatusCode())
                        		.body("Ошибка при проверке: " + response.getBody()));
                    }
                })
		        .onErrorResume(error -> {
		            if (error.getMessage().contains("666")) {
		            	System.err.println("666");
		                return Mono.just(new ResponseEntity<>("Введен неверный код: " + code, HttpStatus.NOT_FOUND));
		            }
		            if (error.getMessage().contains("616")) {
		            	System.err.println("616");
		                return Mono.just(new ResponseEntity<>("Данный код просрочен: " + code, HttpStatus.FORBIDDEN));
		            }
	            	System.err.println(error.getMessage());
		            return Mono.just(new ResponseEntity<>("Ошибка проверки кода: " + error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
		        });
	}
}
