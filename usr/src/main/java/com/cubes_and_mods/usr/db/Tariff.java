package com.cubes_and_mods.usr.db;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
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

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default false")
    @JsonProperty("enabled")
    private Boolean enabled;

    @Column(name = "hours_work_max", nullable = false, columnDefinition = "integer default 24")
    @JsonProperty("hours_work_max")
    private Integer hoursWorkMax;

    @Column(name = "max_players", columnDefinition = "integer default 10")
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