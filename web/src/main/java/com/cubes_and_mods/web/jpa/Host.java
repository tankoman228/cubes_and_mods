package com.cubes_and_mods.web.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class Host {

    private Integer id;

    @JsonProperty("memory_used")
    private Long memoryUsed;

    @JsonProperty("id_client")
    private Integer idClient;

    @JsonProperty("id_tariff")
    private Integer idTariff;

    @JsonProperty("id_server")
    private Integer idServer;

    @JsonProperty("seconds_working")
    private Integer secondsWorking;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("server")
    private Server server; 
    
    @JsonProperty("tariff")
    private Tariff tariff; 
    
    @JsonProperty("client")
    private Client client; 
    
    private Set<HostSharing> hostsSharings; 
    
    private Set<Backup> backups;

    public Host() {}
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getMemoryUsed() {
		return memoryUsed;
	}

	public void setMemoryUsed(Long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	public Integer getIdClient() {
		return idClient;
	}

	public void setIdClient(Integer idClient) {
		this.idClient = idClient;
	}

	public Integer getIdTariff() {
		return idTariff;
	}

	public void setIdTariff(Integer idTariff) {
		this.idTariff = idTariff;
	}

	public Integer getIdServer() {
		return idServer;
	}

	public void setIdServer(Integer idServer) {
		this.idServer = idServer;
	}

	public Integer getSecondsWorking() {
		return secondsWorking;
	}

	public void setSecondsWorking(Integer secondsWorking) {
		this.secondsWorking = secondsWorking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Set<HostSharing> getHostsSharings() {
		return hostsSharings;
	}

	public void setHostsSharings(Set<HostSharing> hostsSharings) {
		this.hostsSharings = hostsSharings;
	}

	public Set<Backup> getBackups() {
		return backups;
	}

	public void setBackups(Set<Backup> backups) {
		this.backups = backups;
	}
	
	@JsonIgnore
	private Set<Order> order;

	public Set<Order> getOrder() {
		return order;
	}

	public void setOrder(Set<Order> order) {
		this.order = order;
	}
}
