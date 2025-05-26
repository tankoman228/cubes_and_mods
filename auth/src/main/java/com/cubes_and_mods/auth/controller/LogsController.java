package com.cubes_and_mods.auth.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.auth.security.LoggerService;
import com.cubes_and_mods.auth.security.ProtectedRequest;
import com.cubes_and_mods.auth.service.ServiceCheckMsSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/microservice_logs")
public class LogsController {
    
	@Autowired
	private ServiceCheckMsSession serviceCheckMsSession;

    @Autowired
    private LoggerService loggerService;

    @PostMapping
    public String postMethodName(@RequestBody ProtectedRequest<Void> request) {
        serviceCheckMsSession.check(request, "admin");
        return loggerService.readLog();
    }
}
