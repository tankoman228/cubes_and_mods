package com.cubes_and_mods.web.jpa;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Backup {

    private Integer id;

    @JsonProperty("id_host")
    private Integer idHost;

    @JsonProperty("size_kb")
    private Long sizeKb;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("name")
    private String name;

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
