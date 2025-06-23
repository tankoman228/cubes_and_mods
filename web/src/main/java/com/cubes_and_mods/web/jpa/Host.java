package com.cubes_and_mods.web.jpa;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@Getter
@Setter
public class Host {

    private Integer id;

    @JsonProperty("memory_used")
    private Long memoryUsed;

    @JsonProperty("seconds_working")
    private Integer secondsWorking;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    private Client clientHost;

    private Server serverHost;

    private Tariff tariffHost;

    @JsonIgnore
    private Set<HostSharing> hostsSharings; 
    
    @JsonIgnore
    private Set<Backup> backups;

	@JsonIgnore
	private Set<Order> order;
}
