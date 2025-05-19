package com.cubes_and_mods.admin.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "backups")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_host", nullable = false)
    @JsonProperty("id_host")
    private Integer idHost;

    @Column(name = "size_kb", nullable = false)
    @JsonProperty("size_kb")
    private Long sizeKb;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(name = "name", length = 64)
    @JsonProperty("name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_host", nullable = false, referencedColumnName = "id", insertable = false, updatable = false)
    @JsonProperty("host")
    private Host host;

    public Backup() {}
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdHost() {
		return idHost;
	}

	public void setIdHost(Integer idHost) {
		this.idHost = idHost;
	}

	public Long getSizeKb() {
		return sizeKb;
	}

	public void setSizeKb(Long sizeKb) {
		this.sizeKb = sizeKb;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	} 
    
    
}
