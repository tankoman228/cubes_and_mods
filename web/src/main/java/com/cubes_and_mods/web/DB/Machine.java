package com.cubes_and_mods.web.DB;

public class Machine {

    private Integer id;
    private String name;
    private String address;
    private String cpuName;
    private Short cpuThreads;
    private Short cpuThreadsFree;
    private Short ram;
    private Short ramFree;
    private Integer memory;
    private Integer memoryFree;

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

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(Integer memoryFree) {
        this.memoryFree = memoryFree;
    }
}