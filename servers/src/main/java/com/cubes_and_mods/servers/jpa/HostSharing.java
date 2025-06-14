package com.cubes_and_mods.servers.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Entity
@Table(name = "host_sharings")
public class HostSharing {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_host", nullable = false)
    @JsonIgnore
    private Host hostHostSharing; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    @JsonIgnore
    private Client clientHostSharing;
}