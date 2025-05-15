package com.cubes_and_mods.web.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class HostSharing {

    @JsonProperty("id")
    private Integer id;
    
    @JsonIgnore
    private Host hostHostSharing; 
    
    @JsonIgnore
    private Client clientHostSharing;
}