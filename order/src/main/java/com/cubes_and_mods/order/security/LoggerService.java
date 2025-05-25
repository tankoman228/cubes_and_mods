package com.cubes_and_mods.order.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

@Service
public class LoggerService {

    private final static Object lock = new Object();

    @Value("${spring.application.name}")
    private String appName;

    public String readLog() {
        var filename = appName + ".log";
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read log file: " + filename, e);
        }
    }

    public void simpleLog(String message) {
        synchronized (lock) {
            log(message);
        }
    }

    public void LogProtectedRequest(ProtectedRequest<?> body, String url) {

        synchronized (lock) {
            log("Получен запрос " + new Date().toString() + " " + url);
            
            if (body.userSession != null) {
                log("Проксирован от авторизованного пользователя: ");
            }
            if (body.serviceSessionId != null) {
                log("Заявленный адрес микросервиса: " + body.serviceSessionId);
            }
            if (body.data != null) {
                log("Содержимое запроса имеет тип: " + body.data.getClass().getName());
            }
            else {
                log("Содержимое пустое");
            }
            log("-------");       
        }
    }

    public void logError(Exception e) {
        synchronized (lock) {
            log("Возникло исключение " + new Date().toString());   
            log(e.getClass().getName());
            log("Текст ошибки: ");
            log(e.getMessage());
            log("Стектрейс (упрощённый): ");
            for(StackTraceElement element : e.getStackTrace()) {
                if(element.toString().contains("cubes_and_mods")) {
                    log(element.toString());
                }
            }
            log("---------");
        }
    }

    private void log(String message) {
        
        try {
            var filename = appName + ".log";
            Files.writeString(
                Paths.get(filename),
                message + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to log file: " , e);
        }
    }
}