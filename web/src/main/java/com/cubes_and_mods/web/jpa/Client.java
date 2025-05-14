package com.cubes_and_mods.web.jpa;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Client {

    private Integer id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("banned")
    private Boolean banned;

    @JsonProperty("additional_info")
    private String additionalInfo;

    private Set<Host> hosts;
    
    private Set<Host> orders;
    
    private Set<HostSharing> host_sharings;

    public Client() {}
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getBanned() {
		return banned;
	}

	public void setBanned(Boolean banned) {
		this.banned = banned;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Set<Host> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}

	public Set<Host> getOrders() {
		return orders;
	}

	public void setOrders(Set<Host> orders) {
		this.orders = orders;
	}

	public Set<HostSharing> getHost_sharings() {
		return host_sharings;
	}

	public void setHost_sharings(Set<HostSharing> host_sharings) {
		this.host_sharings = host_sharings;
	}
}
