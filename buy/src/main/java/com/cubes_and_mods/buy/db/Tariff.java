package com.cubes_and_mods.buy.db;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("name")
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @JsonProperty("cost_rub")
    @Column(name = "cost_rub", nullable = false)
    private Integer costRub;

    @JsonProperty("ram")
    @Column(name = "ram", nullable = false)
    private Short ram;

    @JsonProperty("cpu_threads")
    @Column(name = "cpu_threads", nullable = false)
    private Short cpuThreads;

    @JsonProperty("memory_limit")
    @Column(name = "memory_limit", nullable = false)
    private Integer memoryLimit;

    @JsonProperty("enabled")
    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default false")
    private Boolean enabled;

    @JsonProperty("hours_work_max")
    @Column(name = "hours_work_max", nullable = false, columnDefinition = "integer default 24")
    private Integer hoursWorkMax;

    @JsonProperty("max_players")
    @Column(name = "max_players", columnDefinition = "integer default 10")
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

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
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