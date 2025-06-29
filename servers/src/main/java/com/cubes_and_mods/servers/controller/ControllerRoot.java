package com.cubes_and_mods.servers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.servers.security.LoggerService;
import com.cubes_and_mods.servers.security.ProtectedRequest;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping
public class ControllerRoot {

	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	

        var re = new VerifyWebResponce(r.a + r.b);
        ProtectedRequest.c = String.valueOf(r.a + r.b);
		return new ResponseEntity<>(re, HttpStatus.OK); 
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

    @Autowired
    private LoggerService loggerService;
	
	@PostMapping("microservice_logs")
    @AllowedOrigins({})
	public ResponseEntity<String> logs(ProtectedRequest<Void> request) { 
        return ResponseEntity.ok(loggerService.readLog());
    }
}
