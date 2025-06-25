package com.cubes_and_mods.web.DB;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class Machine {

	    @JsonProperty("id")
	    private Integer id;

	    @JsonProperty("name")
	    private String name;

	    @JsonProperty("address")
	    private String address;

	    @JsonProperty("cpu_name")
	    private String cpuName;

	    @JsonProperty("cpu_threads")
	    private Short cpuThreads;

	    @JsonProperty("cpu_threads_free")
	    private Short cpuThreadsFree;

	    @JsonProperty("ram")
	    private Short ram;

	    @JsonProperty("ram_free")
	    private Short ramFree;

	    @JsonProperty("memory")
	    private Long memory;

	    @JsonProperty("memory_free")
	    private Long memoryFree;

    public Machine() {
        // Конструктор по умолчанию
    }

    // Геттеры и сеттеры

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
}