package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    @JsonProperty("name")
    private String name;

    @Column(name = "cost_rub", nullable = false)
    @JsonProperty("cost_rub")
    private Integer costRub;

    @Column(name = "ram", nullable = false)
    @JsonProperty("ram")
    private Short ram;

    @Column(name = "cpu_threads", nullable = false)
    @JsonProperty("cpu_threads")
    private Short cpuThreads;

    @Column(name = "memory_limit", nullable = false)
    @JsonProperty("memory_limit")
    private Long memoryLimit;

    @Column(name = "enabled", nullable = false)
    @JsonProperty("enabled")
    private Boolean enabled;

    @Column(name = "hours_work_max", nullable = false)
    @JsonProperty("hours_work_max")
    private Integer hoursWorkMax;

    @Column(name = "max_players")
    @JsonProperty("max_players")
    private Integer maxPlayers;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

    public Tariff() {}
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCostRub() {
		return costRub;
	}

	public void setCostRub(Integer costRub) {
		this.costRub = costRub;
	}

	public Short getRam() {
		return ram;
	}

	public void setRam(Short ram) {
		this.ram = ram;
	}

	public Short getCpuThreads() {
		return cpuThreads;
	}

	public void setCpuThreads(Short cpuThreads) {
		this.cpuThreads = cpuThreads;
	}

	public Long getMemoryLimit() {
		return memoryLimit;
	}

	public void setMemoryLimit(Long memoryLimit) {
		this.memoryLimit = memoryLimit;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getHoursWorkMax() {
		return hoursWorkMax;
	}

	public void setHoursWorkMax(Integer hoursWorkMax) {
		this.hoursWorkMax = hoursWorkMax;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(Integer maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public Set<Host> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}

	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
    
    
}
