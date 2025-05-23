package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    @JsonProperty("name")
    private String name;

    @Column(name = "address", length = 64)
    @JsonProperty("address")
    private String address;

    @Column(name = "cpu_name", nullable = false, length = 24)
    @JsonProperty("cpu_name")
    private String cpuName;

    @Column(name = "cpu_threads", nullable = false)
    @JsonProperty("cpu_threads")
    private Short cpuThreads;

    @Column(name = "cpu_threads_free")
    @JsonProperty("cpu_threads_free")
    private Short cpuThreadsFree;

    @Column(name = "ram", nullable = false)
    @JsonProperty("ram")
    private Short ram;

    @Column(name = "ram_free")
    @JsonProperty("ram_free")
    private Short ramFree;

    @Column(name = "memory", nullable = false)
    @JsonProperty("memory")
    private Long memory;

    @Column(name = "memory_free")
    @JsonProperty("memory_free")
    private Long memoryFree;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

    public Server() {}
    
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCpuName() {
		return cpuName;
	}

	public void setCpuName(String cpuName) {
		this.cpuName = cpuName;
	}

	public Short getCpuThreads() {
		return cpuThreads;
	}

	public void setCpuThreads(Short cpuThreads) {
		this.cpuThreads = cpuThreads;
	}

	public Short getCpuThreadsFree() {
		return cpuThreadsFree;
	}

	public void setCpuThreadsFree(Short cpuThreadsFree) {
		this.cpuThreadsFree = cpuThreadsFree;
	}

	public Short getRam() {
		return ram;
	}

	public void setRam(Short ram) {
		this.ram = ram;
	}

	public Short getRamFree() {
		return ramFree;
	}

	public void setRamFree(Short ramFree) {
		this.ramFree = ramFree;
	}

	public Long getMemory() {
		return memory;
	}

	public void setMemory(Long memory) {
		this.memory = memory;
	}

	public Long getMemoryFree() {
		return memoryFree;
	}

	public void setMemoryFree(Long memoryFree) {
		this.memoryFree = memoryFree;
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
