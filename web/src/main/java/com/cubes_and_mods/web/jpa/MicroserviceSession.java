package com.cubes_and_mods.web.jpa;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MicroserviceSession {

    @JsonProperty("ip_port")
    private String ipPort;

    @JsonProperty("last_register")
    private LocalDateTime lastRegister;

    @JsonProperty("first_register")
    private LocalDateTime firstRegister;

    @JsonProperty("alarm")
    private Boolean alarm;

    @JsonProperty("service_type")
    private String serviceType;

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