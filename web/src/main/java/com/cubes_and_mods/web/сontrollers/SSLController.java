package com.cubes_and_mods.web.сontrollers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.security.ProtectedRequest;
import com.cubes_and_mods.web.security.annotations.AllowedOrigins;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/")
public class SSLController {
	
	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	

        if (ProtectedRequest.c != null) return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Чтоюы после регистрации ЭЦП не менялась никогда и никем

		var answ = new ResponseEntity<>(new VerifyWebResponce(r.a + r.b), HttpStatus.OK); 
        ProtectedRequest.c = String.valueOf(r.a + r.b);
        return answ;
	}
	
    public static class VerifyWebRequest { 
        public int a, b;
    }

    public static class VerifyWebResponce {
        public int c;
        @JsonCreator
        public VerifyWebResponce (@JsonProperty("c") int c) {
            this.c = c;
        }
        public VerifyWebResponce () {}
    }

    @PostMapping("/microservice_log")
	@AllowedOrigins({})
	public ResponseEntity<String> microservice_log(@RequestBody ProtectedRequest<Void> body) {
		return ResponseEntity.ok("Данный функционал не реализован микросервисом по причине того, веб-интерфейс для клиентов писал не я");
	}
}
