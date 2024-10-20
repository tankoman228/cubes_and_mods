package com.cubes_and_mods.game.db;

import jakarta.persistence.*;

@Entity
@Table(name = "mineservers")
public class Mineserver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "memory_used", nullable = false, columnDefinition = "integer default 0")
    private Integer memoryUsed;

    @Column(name = "id_user", nullable = false)
    private Integer idUser;

    @Column(name = "id_tariff", nullable = false)
    private Integer idTariff;

    @Column(name = "id_machine", nullable = false)
    private Integer idMachine;

    @Column(name = "seconds_working", nullable = false, columnDefinition = "integer default 0")
    private Integer secondsWorking;

    @Column(name = "ip", nullable = false, length = 25)
    private String ip;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "description", length = 256)
    private String description;

    public Mineserver() {
        // Конструктор по умолчанию
    }

    // Геттеры и сеттеры

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Integer memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdTariff() {
        return idTariff;
    }

    public void setIdTariff(Integer idTariff) {
        this.idTariff = idTariff;
    }

    public Integer getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(Integer idMachine) {
        this.idMachine = idMachine;
    }

    public Integer getSecondsWorking() {
        return secondsWorking;
    }

    public void setSecondsWorking(Integer secondsWorking) {
        this.secondsWorking = secondsWorking;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
