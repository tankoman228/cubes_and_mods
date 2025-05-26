package com.cubes_and_mods.web.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Admin {

    private Integer id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password_hash")
    private String passwordHash;

    @JsonProperty("can_view_stats")
    private Boolean canViewStats;

    @JsonProperty("can_view_logs")
    private Boolean canViewLogs;

    @JsonProperty("can_clients")
    private Boolean canClients;

    @JsonProperty("can_hosts")
    private Boolean canHosts;

    @JsonProperty("can_orders")
    private Boolean canOrders;

    @JsonProperty("can_servers")
    private Boolean canServers;

    @JsonProperty("can_monitor_srv")
    private Boolean canMonitorSrv;

    @JsonProperty("can_tech_support")
    private Boolean canTechSupport;

    @JsonProperty("can_tariffs")
    private Boolean canTariffs;

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
