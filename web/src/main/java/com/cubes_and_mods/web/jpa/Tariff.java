package com.cubes_and_mods.web.jpa;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class Tariff {

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

    @JsonIgnore
    private Set<Host> hosts;
    
    @JsonIgnore
    private Set<Order> orders;
    
}
