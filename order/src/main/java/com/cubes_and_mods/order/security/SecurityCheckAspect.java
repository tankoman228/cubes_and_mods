package com.cubes_and_mods.order.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
/**
 * Спрашивает у auth проверку, валидный ли источник
 */
public class SecurityCheckAspect {

    @Autowired
    private SecurityCheckingService securityCheckingService;

    @Around("@within(restController) || @within(requestMapping)")
    public Object checkSecurity(ProceedingJoinPoint joinPoint, 
                               RestController restController,
                               RequestMapping requestMapping) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        // Получаем ProtectedRequest из аргументов метода
        ProtectedRequest<?> protectedRequest = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ProtectedRequest) {
                protectedRequest = (ProtectedRequest<?>) arg;
                break;
            }
        }

        if (protectedRequest == null) {
            return joinPoint.proceed();
        }

        // Получаем полный путь эндпоинта
        String basePath = getClassMapping(clazz);
        Optional<String> methodPath = getMethodMapping(method);
        if (methodPath.isEmpty()) {
            return joinPoint.proceed();
        }
        String endpoint = combinePaths(basePath, methodPath.get());

        // Выполняем проверку
        if (!securityCheckingService.checkRequest(endpoint, protectedRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return joinPoint.proceed();
    }

    private String getClassMapping(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        return "";
    }

    private Optional<String> getMethodMapping(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            String[] paths = null;
            if (annotation instanceof RequestMapping) {
                paths = ((RequestMapping) annotation).value();
            } else if (annotation instanceof GetMapping) {
                paths = ((GetMapping) annotation).value();
            } else if (annotation instanceof PostMapping) {
                paths = ((PostMapping) annotation).value();
            } else if (annotation instanceof PutMapping) {
                paths = ((PutMapping) annotation).value();
            } else if (annotation instanceof DeleteMapping) {
                paths = ((DeleteMapping) annotation).value();
            }
            if (paths != null && paths.length > 0) {
                return Optional.of(paths[0]);
            }
        }
        return Optional.empty();
    }

    private String combinePaths(String path1, String path2) {
        if (path1.endsWith("/") && path2.startsWith("/")) {
            return path1 + path2.substring(1);
        } else if (!path1.endsWith("/") && !path2.startsWith("/")) {
            return path1 + "/" + path2;
        } else {
            return path1 + path2;
        }
    }
}