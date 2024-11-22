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
			Mono<String> codeMono = mailClient.generateCode(email);
			
		    codeMono.subscribe(code -> {
		        emailSender.sendSimpleEmail(email, "Код доступа", "Ваш код доступа: " + code);
		    }, error -> {
		        if (error instanceof MailException) {
		            MailException mailEx = (MailException) error;
		            System.out.println(mailEx.getMessage());
		            if (mailEx.getMessage().contains("550")) {
		                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный адрес получателя: " + email);
		            }
		            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send email: " + mailEx.getMessage());
		        } else {
		            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка: " + error.getMessage());
		        }
		    });

			//String code = "123456";
			//emailSender.sendSimpleEmail(email, "Код доступа", "Ваш код доступа: "+code);
			return ResponseEntity.ok("code_sended");
		}
		catch (MailException ex){
			System.out.println(ex.getMessage());
            if (ex.getMessage().contains("550")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Неверный адрес получателя: " + email);
            }
            return new ResponseEntity<>("Unable to send email: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
