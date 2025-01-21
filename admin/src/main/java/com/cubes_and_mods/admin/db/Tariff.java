package com.cubes_and_mods.admin.db;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Tariff {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost_rub")
    private Integer costRub;

    @JsonProperty("ram")
    private Short ram;

    @JsonProperty("cpu_threads")
    private Short cpuThreads;

    @JsonProperty("memory_limit")
    private Long memoryLimit;


    @JsonProperty("enabled")
    private Boolean enabled;


    @JsonProperty("hours_work_max")
    private Integer hoursWorkMax;


    @JsonProperty("max_players")
    private Integer maxPlayers;
    
    public Tariff() {
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
}