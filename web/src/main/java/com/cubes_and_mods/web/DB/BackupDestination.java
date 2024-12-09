package com.cubes_and_mods.web.DB;

import java.time.LocalDateTime;

public class BackupDestination {

    private Long id;
    private Integer idMineserver;
    private Integer idMachine;

    public BackupDestination() {
        // Конструктор по умолчанию
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdMineserver() {
        return idMineserver;
    }

    public void setIdMineserver(Integer idMineserver) {
        this.idMineserver = idMineserver;
    }

    public Integer getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(Integer idMachine) {
        this.idMachine = idMachine;
    }
}
