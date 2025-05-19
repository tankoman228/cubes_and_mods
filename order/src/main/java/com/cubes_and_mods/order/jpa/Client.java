package com.cubes_and_mods.order.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, length = 128)
    @JsonProperty("email")
    private String email;

    @Column(name = "password", nullable = false, length = 256)
    @JsonProperty("password")
    private String password;

    @Column(name = "banned", nullable = false)
    @JsonProperty("banned")
    private Boolean banned;

    @Column(name = "additional_info", nullable = false)
    @JsonProperty("additional_info")
    private String additionalInfo;

    @OneToMany(mappedBy = "clientHost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Host> hosts;
    
    @OneToMany(mappedBy = "clientOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;
    
    @OneToMany(mappedBy = "clientHostSharing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HostSharing> host_sharings;
}
