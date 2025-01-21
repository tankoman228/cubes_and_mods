package com.cubes_and_mods.web.сontrollers;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import reactor.core.publisher.Mono;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController{
    private final ErrorAttributes errorAttributes;

    public ErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(WebRequest request, Model model) {
        Throwable error = getError(request);
        String errorMessage = (error != null) ? error.getMessage() : "Неизвестная ошибка";
        HttpStatus status = getStatus(request);
        
        model.addAttribute("errorCode", status.value());
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    private HttpStatus getStatus(WebRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code", WebRequest.SCOPE_REQUEST);
        return (statusCode != null) ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    private Throwable getError(WebRequest request) {
        return errorAttributes.getError(request);
    }

    public String getErrorPath() {
        return "/error";
    }
	
	/*
    private final ErrorAttributes errorAttributes;

    public ErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public Mono<String> handleError(ServerRequest request, Model model) {
        Throwable error = getError(request);
        String errorMessage = (error != null) ? error.getMessage() : "Неизвестная ошибка";
        HttpStatus status = getStatus(request);

        model.addAttribute("errorCode", status.value());
        model.addAttribute("errorMessage", errorMessage);

        return Mono.just("error"); // Название шаблона Thymeleaf без расширения
    }

    private HttpStatus getStatus(ServerRequest request) {
        Integer statusCode = (Integer) request.attribute("javax.servlet.error.status_code").orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return HttpStatus.valueOf(statusCode);
    }

    private Throwable getError(ServerRequest request) {
        return errorAttributes.getError(request);
    }*/
}
