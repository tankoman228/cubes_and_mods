package com.cubes_and_mods.order.security;

import java.lang.annotation.Retention;
import  java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    SuspiciousLevel suspicion();

    public enum SuspiciousLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    boolean fromClient() default false;
}
