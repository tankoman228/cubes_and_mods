package com.cubes_and_mods.order.db;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "backups")
public class Backup {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "id_mineserver", nullable = false)
	    @JsonProperty("id_mineserver")
	    private Integer idMineserver;

	    @Column(name = "size_kb", nullable = false)
	    @JsonProperty("size_kb")
	    private Long sizeKb;

	    @Column(name = "created_at", nullable = false, updatable = false)
	    @JsonProperty("created_at")
	    private LocalDateTime createdAt;

	    @Column(name = "name", length = 64)
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
