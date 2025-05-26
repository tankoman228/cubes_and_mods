package com.cubes_and_mods.host.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "hosts")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "memory_used", nullable = false)
    @JsonProperty("memory_used")
    private Long memoryUsed;

    @Column(name = "seconds_working", nullable = false)
    @JsonProperty("seconds_working")
    private Integer secondsWorking;

    @Column(name = "name", length = 64, nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "description", length = 256, nullable = false)
    @JsonProperty("description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client clientHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_server")
    private Server serverHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tariff")
    private Tariff tariffHost;

    @OneToMany(mappedBy = "hostHostSharing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<HostSharing> hostsSharings; 
    
    @OneToMany(mappedBy = "hostBackup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Backup> backups;

	@OneToMany(mappedBy = "hostOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Order> order;
}
