package com.cubes_and_mods.web.DB;

public class Tariff {

    private Integer id;

    private String name;

    private Integer costRub;

    private Short ram;

    private Short cpuThreads;

    private Integer memoryLimit;

    private Boolean enabled;

    private Integer hoursWorkMax;

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