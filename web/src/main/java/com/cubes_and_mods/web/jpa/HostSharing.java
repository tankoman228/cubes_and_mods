package com.cubes_and_mods.web.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class HostSharing {

    @JsonProperty("id_host")
    private Integer idHost;

    @JsonProperty("id_client")
    private Integer idClient;
    
    @JsonIgnore
    private Host host; 
    
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