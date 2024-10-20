package com.cubes_and_mods.buy.db;

import jakarta.persistence.*;


@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "address", length = 64)
    private String address;

    @Column(name = "cpu_name", nullable = false, length = 24)
    private String cpuName;

    @Column(name = "cpu_threads", nullable = false)
    private Short cpuThreads;

    @Column(name = "cpu_threads_free")
    private Short cpuThreadsFree;

    @Column(name = "ram", nullable = false)
    private Short ram;

    @Column(name = "ram_free")
    private Short ramFree;

    @Column(name = "memory", nullable = false)
    private Integer memory;

    @Column(name = "memory_free")
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