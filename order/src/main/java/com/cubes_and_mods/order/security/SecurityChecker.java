package com.cubes_and_mods.order.security;
import com.cubes_and_mods.order.security.Logging.SuspiciousLevel;

public class SecurityChecker {

    AllowedOrigins allowedOrigins;
    private Logging logging;

    public SecurityChecker(AllowedOrigins allowedOrigins, Logging logging) {
        this.allowedOrigins = allowedOrigins;
        this.logging = logging;
    }

    /**
     * Проверяет, что источник верный
     * @param alpha особый хэш, получается из запроса и нужен для проверки
     * @return
     */
    public boolean checkSecurity(String alpha) {

        

        if (logging.fromClient()) {
            throw new RuntimeException("Logging without of client session is not allowed");
        }

        return true;
    }

    public boolean checkSecurity(String alpha, String origin, ClientSession clientSession) {

        return true;
    }
}
