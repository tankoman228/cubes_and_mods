package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", length = 64)
    @JsonProperty("username")
    private String username;

    @Column(name = "password_hash", length = 256)
    @JsonProperty("password_hash")
    private String passwordHash;

    @Column(name = "can_view_stats")
    @JsonProperty("can_view_stats")
    private Boolean canViewStats;

    @Column(name = "can_view_logs")
    @JsonProperty("can_view_logs")
    private Boolean canViewLogs;

    @Column(name = "can_clients")
    @JsonProperty("can_clients")
    private Boolean canClients;

    @Column(name = "can_hosts")
    @JsonProperty("can_hosts")
    private Boolean canHosts;

    @Column(name = "can_orders")
    @JsonProperty("can_orders")
    private Boolean canOrders;

    @Column(name = "can_servers")
    @JsonProperty("can_servers")
    private Boolean canServers;

    @Column(name = "can_monitor_srv")
    @JsonProperty("can_monitor_srv")
    private Boolean canMonitorSrv;

    @Column(name = "can_tech_support")
    @JsonProperty("can_tech_support")
    private Boolean canTechSupport;

    @Column(name = "can_tariffs")
    @JsonProperty("can_tariffs")
    private Boolean canTariffs;

    @Column(name = "can_admins")
    @JsonProperty("can_admins")
    private Boolean canAdmins;
    
    public Admin() {
		canViewStats = true;
		canViewLogs = true;
		canClients = true;
		canHosts = true;
		canOrders = true;
		canServers = true;
		canMonitorSrv = true;
		canTechSupport = true;
		canTariffs = true;
		canAdmins = true;
		
	}
}
