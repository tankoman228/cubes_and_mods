package com.cubes_and_mods.web.DB;

import java.time.LocalDateTime;

public class Backup {

    private Integer id;
    private Integer idMineserver;
    private Integer sizeKb;
    private LocalDateTime createdAt;
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

    public Integer getSizeKb() {
        return sizeKb;
    }

    public void setSizeKb(Integer sizeKb) {
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
