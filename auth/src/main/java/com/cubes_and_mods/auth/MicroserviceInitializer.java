package com.cubes_and_mods.auth;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@Component
public class MicroserviceInitializer {

    public static HashMap<String, Object> registeredEndpoints = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("----- Registered Endpoints -----");

        String packageName = this.getClass().getPackageName();

        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packageName)
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .scan()) {

            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                try {
                    Class<?> clazz = Class.forName(classInfo.getName());

                    // Проверяем, является ли класс контроллером
                    if (clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(RequestMapping.class)) {
                        System.out.println("📌 Controller: " + clazz.getSimpleName());

                        // Получаем базовый путь класса из @RequestMapping
                        String basePath = getClassMapping(clazz);

                        // Перебираем методы класса
                        for (Method method : clazz.getDeclaredMethods()) {
                            Optional<String> endpoint = getMethodMapping(method);

                            endpoint.ifPresent(path -> {
                                System.out.println("  🛠  Method: " + method.getName());
                                System.out.println("  🔗 Path: " + basePath + path);
                                System.out.println("  🎭 Annotations: " + Arrays.toString(method.getAnnotations()));
                                System.out.println();
                            });
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("----- Registered Endpoints -----");
    }

    // Получает путь, заданный в @RequestMapping класса
    private String getClassMapping(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
            return mapping.value().length > 0 ? mapping.value()[0] : "";
        }
        return "";
    }

    // Получает путь, заданный в @GetMapping, @PostMapping и т. д.
    private Optional<String> getMethodMapping(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return Optional.of(getPath(method.getAnnotation(GetMapping.class).value()));
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            return Optional.of(getPath(method.getAnnotation(PostMapping.class).value()));
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return Optional.of(getPath(method.getAnnotation(PutMapping.class).value()));
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return Optional.of(getPath(method.getAnnotation(DeleteMapping.class).value()));
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return Optional.of(getPath(method.getAnnotation(RequestMapping.class).value()));
        }
        return Optional.empty();
    }

    // Берёт первый путь из массива значений аннотации
    private String getPath(String[] paths) {
        return (paths.length > 0) ? paths[0] : "";
    }
}