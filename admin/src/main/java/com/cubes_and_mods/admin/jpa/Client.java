package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, length = 128)
    @JsonProperty("email")
    private String email;

    @Column(name = "password", nullable = false, length = 256)
    @JsonProperty("password")
    private String password;

    @Column(name = "banned", nullable = false)
    @JsonProperty("banned")
    private Boolean banned;

    @Column(name = "additional_info", nullable = false)
    @JsonProperty("additional_info")
    private String additionalInfo;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Host> orders;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
