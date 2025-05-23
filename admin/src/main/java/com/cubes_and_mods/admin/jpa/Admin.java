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
    private String username;

    @Column(name = "password_hash", length = 256)
    private String passwordHash;

    @Column(name = "can_view_stats")
    private Boolean canViewStats;

    @Column(name = "can_view_logs")
    private Boolean canViewLogs;

    @Column(name = "can_clients")
    private Boolean canClients;

    @Column(name = "can_hosts")
    private Boolean canHosts;

    @Column(name = "can_orders")
    private Boolean canOrders;

    @Column(name = "can_servers")
    private Boolean canServers;

    @Column(name = "can_monitor_srv")
    private Boolean canMonitorSrv;

    @Column(name = "can_tech_support")
    private Boolean canTechSupport;

    @Column(name = "can_tariffs")
    private Boolean canTariffs;

    @Column(name = "can_admins")
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
