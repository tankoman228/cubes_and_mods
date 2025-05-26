package com.cubes_and_mods.servers.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.cubes_and_mods.servers.security.annotations.CheckUserSession;
import com.cubes_and_mods.servers.security.annotations.CheckUserSession.SessionValidator;

@Aspect
@Component
@Order(2) 
/**
 * Спрашивает у auth проверку, валидный ли источник
 */
public class SecurityCheckAspect {

    @Autowired
    private SecurityCheckingService securityCheckingService;

    @Autowired 
    private ServiceClientSession sessionClient;

    @Autowired
    private ApplicationContext ctx;


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

    @Around("@annotation(check)")
    public Object around(ProceedingJoinPoint joinPoint, CheckUserSession check) throws Throwable {

        System.out.println("Aspect check client session entered!!!!!!!!!");
        if (IgnoreSourceChecking) {
            return joinPoint.proceed();
        }

        // Ищем ProtectedRequest в аргументах метода
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ProtectedRequest) {

                ProtectedRequest<?> protectedRequest = (ProtectedRequest<?>) arg;
                ClientSession session = sessionClient.getSession(protectedRequest.userSession);

                if (session == null)
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                

                SessionValidator validator = (SessionValidator) ctx.getBean(check.validator());

                boolean ok = validator.validate(session, protectedRequest);
                if (!ok) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                break;
            }
        }

        return joinPoint.proceed();
    }
}