package com.cubes_and_mods.host.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(1) 
public class LoggerAspect {

    @Autowired
    private LoggerService loggerService;
    
    @Around("@annotation(mapping)")
    public Object getAspect(ProceedingJoinPoint joinPoint, GetMapping mapping) throws Throwable  {
        return check(joinPoint);
    }

    @Around("@annotation(mapping)")
    public Object postAspect(ProceedingJoinPoint joinPoint, PostMapping mapping) throws Throwable  {
        return check(joinPoint);
    }

    @Around("@annotation(mapping)")
    public Object putAspect(ProceedingJoinPoint joinPoint, PutMapping mapping)  throws Throwable {
        return check(joinPoint);
    }

    @Around("@annotation(mapping)")
    public Object deleteAspect(ProceedingJoinPoint joinPoint, DeleteMapping mapping) throws Throwable  {
        return check(joinPoint);
    }

    private Object check(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof ProtectedRequest) {               
                loggerService.LogProtectedRequest((ProtectedRequest<?>) arg, method + " " + uri); 
                break;
            }
        }
        loggerService.simpleLog("Запрос без ProtectedRequest " + method + " " + uri);

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?> response && response.getStatusCode().value() != 200) {
                loggerService.simpleLog("КОД ОШИБКИ " + response.getStatusCode().value());
            }
            else {
                loggerService.simpleLog("Запрос успешный, но не содержит сущности, " + method + " " + uri);
            }

            return result;
        } catch (Exception e) {
            loggerService.logError(e);
            throw e;
        }
    }
}
