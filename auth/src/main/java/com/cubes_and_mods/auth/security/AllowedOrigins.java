package com.cubes_and_mods.auth.security;

import java.lang.annotation.Retention;
import  java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedOrigins {

    MService[] value();

    public enum MService {
        ORDER,
        WEB,
        SERVERS,
        HOST   
    }  
}
