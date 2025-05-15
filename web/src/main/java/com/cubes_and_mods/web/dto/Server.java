package com.cubes_and_mods.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class Server {

    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("cpu_name")
    private String cpuName;

    @JsonProperty("cpu_threads")
    private Short cpuThreads;

    @JsonProperty("cpu_threads_free")
    private Short cpuThreadsFree;

    @JsonProperty("ram")
    private Short ram;

    @JsonProperty("ram_free")
    private Short ramFree;

    @JsonProperty("memory")
    private Long memory;

    @JsonProperty("memory_free")
    private Long memoryFree;
    
    @JsonIgnore
    private Set<Host> hosts;
    
    @JsonIgnore
    private Set<Order> orders;
}
