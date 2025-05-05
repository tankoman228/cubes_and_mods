package com.cubes_and_mods.servers.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.servers.security.annotations.AllowedOrigins;
import com.cubes_and_mods.servers.security.annotations.Logging;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

@Component
public class MicroserviceInitializer {

	@Value("${server.port}")
	private String port; 

    @Autowired
    private SecurityCheckingService securityCheckingService;
	
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
    	
        RegisterMsRequest request = new RegisterMsRequest("servers", port);
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
       
        WebClient webClient = WebClient.builder()
                .baseUrl("https://localhost:8085/") //TODO: подгружать адрес из конфига
                .clientConnector(ClientConnectorForKey.getForKey("auth"))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                        .build())
                .build();
        
        webClient.put()
            .uri("/microservice/register")
            .bodyValue(request)
            .retrieve()
            .toEntity(String.class)
            .doOnSuccess(response -> 
            {
                System.out.println("Регистрация успешна: " + response.getStatusCode());
                ProtectedRequest.serviceSessionIdGlobal = response.getBody();
            })
            .doOnError(error -> System.err.println("Ошибка регистрации: " + error.getMessage()))
            .subscribe();
    }
    
	public class RegisterMsRequest {
		public RegisterMsRequest(String string, String string2) {
			this.ms_type = string;
			port = string2;
		}
		public String ms_type;
		public String port;
	}

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady2() {
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
                                System.out.println("  🛠 Method: " + method.getName());
                                System.out.println("  🔗 Path: " + basePath + path);
                                System.out.println("  🎭 Annotations: " + Arrays.toString(method.getAnnotations()));
                                System.out.println();

                                AllowedOrigins allowedOrigins = method.getAnnotation(AllowedOrigins.class);
                                Logging logging = method.getAnnotation(Logging.class);
                        
                                SecurityChecker securityChecker = new SecurityChecker(allowedOrigins, logging);
                                securityCheckingService.addForEndpoint(basePath + path, securityChecker);
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