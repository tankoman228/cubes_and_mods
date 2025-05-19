package com.cubes_and_mods.host;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {
	
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(10); // For backups operations only! Backups of minecraft servers
    }
}