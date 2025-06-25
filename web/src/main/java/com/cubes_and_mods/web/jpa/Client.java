package com.cubes_and_mods.web.jpa;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class Client {

    private Integer id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("banned")
    private Boolean banned;

    @JsonProperty("additional_info")
    private String additionalInfo;

    private Set<Host> hosts;
    
    private Set<Order> orders;
    
    private Set<HostSharing> host_sharings;
}
