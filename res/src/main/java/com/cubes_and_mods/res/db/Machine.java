package com.cubes_and_mods.res.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("name")
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @JsonProperty("address")
    @Column(name = "address", length = 64)
    private String address;

    @JsonProperty("cpu_name")
    @Column(name = "cpu_name", nullable = false, length = 24)
    private String cpu_name;

    @JsonProperty("cpu_threads")
    @Column(name = "cpu_threads", nullable = false)
    private Short cpu_threads;

    @JsonProperty("cpu_threads_free")
    @Column(name = "cpu_threads_free")
    private Short cpu_threads_free;

    @JsonProperty("ram")
    @Column(name = "ram", nullable = false)
    private Short ram;

    @JsonProperty("ram_free")
    @Column(name = "ram_free")
    private Short ram_free;

    @JsonProperty("memory")
    @Column(name = "memory", nullable = false)
    private Integer memory;

    @JsonProperty("memory_free")
    @Column(name = "memory_free")
    private Integer memory_free;


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
        return cpu_name;
    }

    public void setCpuName(String cpuName) {
        this.cpu_name = cpuName;
    }

    public Short getCpuThreads() {
        return cpu_threads;
    }

    public void setCpuThreads(Short cpuThreads) {
        this.cpu_threads = cpuThreads;
    }

    public Short getCpuThreadsFree() {
        return cpu_threads_free;
    }

    public void setCpuThreadsFree(Short cpuThreadsFree) {
        this.cpu_threads_free = cpuThreadsFree;
    }

    public Short getRam() {
        return ram;
    }

    public void setRam(Short ram) {
        this.ram = ram;
    }

    public Short getRamFree() {
        return ram_free;
    }

    public void setRamFree(Short ramFree) {
        this.ram_free = ramFree;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getMemoryFree() {
        return memory_free;
    }

    public void setMemoryFree(Integer memoryFree) {
        this.memory_free = memoryFree;
    }
}