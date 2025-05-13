package com.cubes_and_mods.host.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import com.cubes_and_mods.host.security.annotations.AllowedOrigins;


@Aspect
@Component
/**
 * Спрашивает у auth проверку, валидный ли источник
 */
public class SecurityCheckAspect {

    @Autowired
    private SecurityCheckingService securityCheckingService;

    @Value("#{new Boolean('${ignore-source-checking}')}")
    private Boolean IgnoreSourceChecking = false;


    @Around("@annotation(allowedOrigins)")
    public Object checkOrigin(ProceedingJoinPoint joinPoint, AllowedOrigins allowedOrigins) throws Throwable {
        
        System.out.println("Aspect check entered!!!!!!!!!");
        if (IgnoreSourceChecking) {
            return joinPoint.proceed();
        }

        // Ищем ProtectedRequest в аргументах метода
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ProtectedRequest) {
                ProtectedRequest<?> protectedRequest = (ProtectedRequest<?>) arg;
                if (!securityCheckingService.checkRequest(allowedOrigins, protectedRequest)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                break;
            }
        }
        
        return joinPoint.proceed();
    }
}