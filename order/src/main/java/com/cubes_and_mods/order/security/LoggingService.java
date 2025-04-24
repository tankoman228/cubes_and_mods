package com.cubes_and_mods.order.security;

import org.springframework.stereotype.Service;

import com.cubes_and_mods.order.security.Logging.SuspiciousLevel;

@Service
public class LoggingService {

    public void log(String message, SuspiciousLevel suspiciousLevel) {
        throw new RuntimeException("Logging is not implemented");
    }

    
}
