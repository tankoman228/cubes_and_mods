package com.cubes_and_mods.servers.jpa;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

@Entity
@Table(name = "hosts")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "memory_used", nullable = false)
    @JsonProperty("memory_used")
    private Long memoryUsed;

    @Column(name = "id_client", nullable = false)
    @JsonProperty("id_client")
    private Integer idClient;

    @Column(name = "id_tariff", nullable = false)
    @JsonProperty("id_tariff")
    private Integer idTariff;

    @Column(name = "id_server", nullable = false)
    @JsonProperty("id_server")
    private Integer idServer;

    @Column(name = "seconds_working", nullable = false)
    @JsonProperty("seconds_working")
    private Integer secondsWorking;

    @Column(name = "name", length = 64, nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "description", length = 256, nullable = false)
    @JsonProperty("description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_server", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("server")
    private Server server; 
    
    @ManyToOne
    @JoinColumn(name = "id_tariff", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("tariff")
    private Tariff tariff; 
    
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("client")
    private Client client; 
    
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HostSharing> hostsSharings; 
    
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Order> order;

	public Set<Order> getOrder() {
	return order;
	}

	public void setOrder(Set<Order> order) {
	this.order = order;
	}
}
