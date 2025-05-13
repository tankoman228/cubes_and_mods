package com.cubes_and_mods.auth.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "order")  // Экранирование имени таблицы из-за использования зарезервированного слова
public class Order {

    @Id
    @Column(name = "code", nullable = false, length = 128)
    @JsonProperty("code")
    private String code;

    @Column(name = "id_client", nullable = false)
    @JsonProperty("id_client")
    private Integer idClient;

    @Column(name = "id_server")
    @JsonProperty("id_server")
    private Integer idServer;

    @Column(name = "id_tariff", nullable = false)
    @JsonProperty("id_tariff")
    private Integer idTariff;

	@Column(name = "id_host", nullable = true)
    @JsonProperty("id_server")
    private Integer idHost;

    @Column(name = "made_at", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    @JsonProperty("made_at")
    private LocalDateTime madeAt;

    @Column(name = "confirmed", nullable = false)
    @JsonProperty("confirmed")
    private Boolean confirmed;

    @Column(name = "closed_at")
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("client")
    private Client client; 
    
    @ManyToOne
    @JoinColumn(name = "id_server", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("server")
    private Server server; 
    
    @ManyToOne
    @JoinColumn(name = "id_tariff", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("tariff")
    private Tariff tariff;

	@ManyToOne
	@JoinColumn(name = "id_host", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
	private Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

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

    public Integer getIdHost() {
		return idHost;
	}

	public void setIdHost(Integer idHost) {
		this.idHost = idHost;
	}
}
