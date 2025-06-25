package com.cubes_and_mods.web.DB;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class Backup {

	    private Integer id;

	    @JsonProperty("id_mineserver")
	    private Integer idMineserver;

	    @JsonProperty("size_kb")
	    private Long sizeKb;

	    @JsonProperty("created_at")
	    private LocalDateTime createdAt;

	    @JsonProperty("name")
	    private String name;

    public Backup() {
        // Конструктор по умолчанию
    }

    // Getters и Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdMineserver() {
        return idMineserver;
    }

    public void setIdMineserver(Integer idMineserver) {
        this.idMineserver = idMineserver;
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
}
