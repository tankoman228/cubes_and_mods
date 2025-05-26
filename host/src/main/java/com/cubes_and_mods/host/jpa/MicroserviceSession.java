package com.cubes_and_mods.host.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Getter
@Setter
@Table(name = "microservice_sessions")
public class MicroserviceSession {

    @Id
    @Column(name = "ip_port", nullable = false, length = 25)
    @JsonProperty("ip_port")
    private String ipPort;

    @Column(name = "last_register", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    @JsonProperty("last_register")
    private LocalDateTime lastRegister;

    @Column(name = "first_register", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    @JsonProperty("first_register")
    private LocalDateTime firstRegister;

    @Column(name = "alarm")
    @JsonProperty("alarm")
    private Boolean alarm;

    @Column(name = "service_type", nullable = false, length = 25)
    @JsonProperty("service_type")
    private String serviceType;

    @Column(name = "banned", nullable = false)
    @JsonProperty("banned")
    private Boolean banned;

    public MicroserviceSession() {}
}