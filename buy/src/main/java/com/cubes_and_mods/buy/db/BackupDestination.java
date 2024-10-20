package com.cubes_and_mods.buy.db;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "backups_destinations")
public class BackupDestination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_mineserver", nullable = false)
    private Integer idMineserver;

    @Column(name = "id_machine", nullable = false)
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
