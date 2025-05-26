package com.cubes_and_mods.web.DB;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mineserver {
	
    
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("memory_used")
    private Long memoryUsed;

    @JsonProperty("id_user")
    private Integer idUser;

    @JsonProperty("id_tariff")
    private Integer idTariff;

    @JsonProperty("id_machine")
    private Integer idMachine;

    @JsonProperty("seconds_working")
    private Integer secondsWorking;

    /*
    @JsonProperty("ip")
    private String ip;
	*/

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
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

    public Long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Long memoryUsed) {
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

    /*
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    */
    
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
