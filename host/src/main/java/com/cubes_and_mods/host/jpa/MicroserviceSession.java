package com.cubes_and_mods.host.jpa;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
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
    
	public String getIpPort() {
		return ipPort;
	}

	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}

	public LocalDateTime getLastRegister() {
		return lastRegister;
	}

	public void setLastRegister(LocalDateTime lastRegister) {
		this.lastRegister = lastRegister;
	}

	public LocalDateTime getFirstRegister() {
		return firstRegister;
	}

	public void setFirstRegister(LocalDateTime firstRegister) {
		this.firstRegister = firstRegister;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Boolean getBanned() {
		return banned;
	}

	public void setBanned(Boolean banned) {
		this.banned = banned;
	}

    
    
}