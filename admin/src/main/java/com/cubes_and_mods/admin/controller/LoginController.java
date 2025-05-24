package com.cubes_and_mods.admin.controller;


import java.io.Console;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Admin;
import com.cubes_and_mods.admin.jpa.repos.AdminRepos;
import com.cubes_and_mods.admin.service.ServiceLogin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/loginn")
public class LoginController {

    @Autowired
    private ServiceLogin serviceLogin;

    @Autowired
    private AdminRepos userRepository;


    private volatile Map<String, Integer> loginAttempts = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Admin User, HttpServletRequest request) {

        String key = User.getUsername() + request.getRemoteAddr() + "_" + System.currentTimeMillis() / 3600000;
        loginAttempts.merge(key, 0, Integer::sum);
        if (loginAttempts.get(key) > 2) {
            return new ResponseEntity<>("Лимит попыток входа временно израсходован!", HttpStatus.FORBIDDEN);
        }

        HttpStatus result = serviceLogin.login(User);

        if (result == HttpStatus.OK) {
            makeSession(request, User);
            loginAttempts.remove(key);
            return ResponseEntity.ok().build();
        }
        else {   
            loginAttempts.merge(key, 1, Integer::sum);
        }
        return new ResponseEntity<>(result);
    }

    @PostMapping("/logout")    
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        // Удаляем аутентификацию из SecurityContext
        SecurityContextHolder.clearContext();

        // Завершаем текущую сессию
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/first_register")
    public ResponseEntity<Map<String, String>> firstRegister(@RequestBody Admin User, HttpServletRequest request) {

        System.out.println("firstRegister");
        System.out.println(User.getUsername());
        System.out.println(User.getPasswordHash().length());

        String error = serviceLogin.firstRegister(User);
        if (error != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", error);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        else {
            makeSession(request, User);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/session_info")
    public ResponseEntity<Admin> getSessionPublicInfo(HttpServletRequest request) {

        if (request.getSession() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        var user = userRepository.findById(userId).get();
        user.setPasswordHash(null);

        return ResponseEntity.ok(user);
    }

    private void makeSession(HttpServletRequest request, Admin User) {

        var user = userRepository.findByEmail(User.getUsername()).get();

        // Сохраняем только данные о пользователе в сессии
        request.getSession().setAttribute("userId", user.getId());
    
        // Устанавливаем токен для безопасности
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, Collections.emptyList());
        
        SecurityContextHolder.getContext().setAuthentication(token);
        
        // Устанавливаем SecurityContext в сессии
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }
    
    
}