package com.cubes_and_mods.web.jpa;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Order {

    @JsonProperty("code")
    private String code;

    @JsonProperty("id_client")
    private Integer idClient;

    @JsonProperty("id_server")
    private Integer idServer;

    @JsonProperty("id_tariff")
    private Integer idTariff;

    @JsonProperty("made_at")
    private LocalDateTime madeAt;

    @JsonProperty("confirmed")
    private Boolean confirmed;

    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    
    @JsonProperty("client")
    private Client client; 
    
    @JsonProperty("server")
    private Server server; 
    
    @JsonProperty("tariff")
    private Tariff tariff;

    public Order() {}
    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIdClient() {
		return idClient;
	}

	public void setIdClient(Integer idClient) {
		this.idClient = idClient;
	}

	public Integer getIdServer() {
		return idServer;
	}

	public void setIdServer(Integer idServer) {
		this.idServer = idServer;
	}

	public Integer getIdTariff() {
		return idTariff;
	}

	public void setIdTariff(Integer idTariff) {
		this.idTariff = idTariff;
	}

	public LocalDateTime getMadeAt() {
		return madeAt;
	}

	public void setMadeAt(LocalDateTime madeAt) {
		this.madeAt = madeAt;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	} 

	private Host host;

	public Host getHost() {
	return host;
	}

	public void setHost(Host host) {
	this.host = host;
	} 
}
