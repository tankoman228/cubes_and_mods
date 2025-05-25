package com.cubes_and_mods.admin.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cubes_and_mods.admin.jpa.repos.AdminRepos;
import com.cubes_and_mods.admin.security.LoggerService;
import com.cubes_and_mods.admin.security.ProtectedRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Returns index.html with API from API.json
*/
@Controller
public class WebController {

    @ResponseBody
	@PostMapping("/verify_ssl")
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

    @Autowired
    private LoggerService loggerService;

    @ResponseBody
	@PostMapping("/microservice_logs")
	public ResponseEntity<String> logs(@RequestBody ProtectedRequest<Void> r, HttpServletRequest request) { 	

        loggerService.simpleLog("Получен запрос на логи микросервиса");
        loggerService.LogProtectedRequest(r, request.getRemoteAddr());

        if (!request.getRemoteAddr().equals(request.getLocalAddr())) {
            loggerService.simpleLog("Отказано: нельзя получать логи с другого сервера админ-панели");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Отказано: нельзя получать логи с другого сервера админ-панели");
        }

        loggerService.simpleLog("Отправлен ответ на логи микросервиса");

		return ResponseEntity.ok(loggerService.readLog());
	}

    @Autowired
    private AdminRepos adminRepos;

    @GetMapping("/login")
    public String loginPage(Model model) throws Exception {

        if (adminRepos.count() == 0) {
            return "first_register";
        }
        return "login";
    }

    @GetMapping("/home")
    public String homePage(Model model) throws Exception {
        return "home";
    }

    @GetMapping("/admins")
    public String adminsPage(Model model) throws Exception {
        return "admins";
    }

    @GetMapping("/clients")
    public String clientsPage(Model model) throws Exception {
        return "clients";
    }

    @GetMapping("/first_register")
    public String firstRegisterPage(Model model) throws Exception {
        return "first_register";
    }

    @GetMapping("/hosts")
    public String hostsPage(Model model) throws Exception {
        return "hosts";
    }

    @GetMapping("/monitoring")
    public String monitoringPage(Model model) throws Exception {
        return "monitoring";
    }

    @GetMapping("/logs")
    public String logsPage(Model model) throws Exception {
        return "logs";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) throws Exception {
        return "orders";
    }

    @GetMapping("/servers")
    public String serversPage(Model model) throws Exception {
        return "servers";
    }

    @GetMapping("/stats")
    public String statsPage(Model model) throws Exception {
        return "stats";
    }

    @GetMapping("/tariffs")
    public String tariffsPage(Model model) throws Exception {
        return "tariffs";
    }

    @GetMapping("/versions")
    public String versionsPage(Model model) throws Exception {
        return "versions";
    }
}