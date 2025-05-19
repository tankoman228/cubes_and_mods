package com.cubes_and_mods.servers.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
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
    
    @OneToMany(mappedBy = "serverHost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "serverOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders;
}
