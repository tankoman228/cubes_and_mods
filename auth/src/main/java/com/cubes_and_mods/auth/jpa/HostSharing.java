package com.cubes_and_mods.auth.jpa;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "host_sharings")
public class HostSharing {

    @Id
    @Column(name = "id_host", nullable = false)
    @JsonProperty("id_host")
    private Integer idHost;

    @Column(name = "id_client")
    @JsonProperty("id_client")
    private Integer idClient;
    
    @ManyToOne
    @JoinColumn(name = "id_host", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Host host; 
    
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Client client;

    public HostSharing() {}
    
	public Integer getIdHost() {
		return idHost;
	}

	public void setIdHost(Integer idHost) {
		this.idHost = idHost;
	}

	public Integer getIdClient() {
		return idClient;
	}

	public void setIdClient(Integer idClient) {
		this.idClient = idClient;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	} 

    
}