package com.cubes_and_mods.order.security;
import org.springframework.web.reactive.function.client.WebClient;

import com.cubes_and_mods.order.security.annotations.AllowedOrigins;
import com.cubes_and_mods.order.security.annotations.Logging;
import com.cubes_and_mods.order.security.annotations.Logging.SuspiciousLevel;

public class SecurityChecker {

    public AllowedOrigins allowedOrigins;
    public Logging logging;

    public SecurityChecker(AllowedOrigins allowedOrigins, Logging logging) {
        this.allowedOrigins = allowedOrigins;
        this.logging = logging;
    }
}
