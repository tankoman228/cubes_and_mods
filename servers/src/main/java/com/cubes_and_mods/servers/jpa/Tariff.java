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
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "enabled", nullable = false)
    @JsonProperty("enabled")
    private Boolean enabled;

    @Column(name = "hours_work_max", nullable = false)
    @JsonProperty("hours_work_max")
    private Integer hoursWorkMax;

    @Column(name = "max_players")
    @JsonProperty("max_players")
    private Integer maxPlayers;

    @OneToMany(mappedBy = "tariffHost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "tariffOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders;
    
}
