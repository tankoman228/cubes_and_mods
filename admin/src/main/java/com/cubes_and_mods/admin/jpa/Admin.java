package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
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
    
    public Admin() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Boolean getCanViewStats() {
		return canViewStats;
	}

	public void setCanViewStats(Boolean canViewStats) {
		this.canViewStats = canViewStats;
	}

	public Boolean getCanViewLogs() {
		return canViewLogs;
	}

	public void setCanViewLogs(Boolean canViewLogs) {
		this.canViewLogs = canViewLogs;
	}

	public Boolean getCanClients() {
		return canClients;
	}

	public void setCanClients(Boolean canClients) {
		this.canClients = canClients;
	}

	public Boolean getCanHosts() {
		return canHosts;
	}

	public void setCanHosts(Boolean canHosts) {
		this.canHosts = canHosts;
	}

	public Boolean getCanOrders() {
		return canOrders;
	}

	public void setCanOrders(Boolean canOrders) {
		this.canOrders = canOrders;
	}

	public Boolean getCanServers() {
		return canServers;
	}

	public void setCanServers(Boolean canServers) {
		this.canServers = canServers;
	}

	public Boolean getCanMonitorSrv() {
		return canMonitorSrv;
	}

	public void setCanMonitorSrv(Boolean canMonitorSrv) {
		this.canMonitorSrv = canMonitorSrv;
	}

	public Boolean getCanTechSupport() {
		return canTechSupport;
	}

	public void setCanTechSupport(Boolean canTechSupport) {
		this.canTechSupport = canTechSupport;
	}

	public Boolean getCanTariffs() {
		return canTariffs;
	}

	public void setCanTariffs(Boolean canTariffs) {
		this.canTariffs = canTariffs;
	}

	public Boolean getCanAdmins() {
		return canAdmins;
	}

	public void setCanAdmins(Boolean canAdmins) {
		this.canAdmins = canAdmins;
	}
}
