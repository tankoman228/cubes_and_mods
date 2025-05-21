package com.cubes_and_mods.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.security.ProtectedRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/")
public class RootController {
	
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
}