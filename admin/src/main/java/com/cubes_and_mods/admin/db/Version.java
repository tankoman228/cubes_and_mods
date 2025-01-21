package com.cubes_and_mods.admin.db;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Version {

	    @JsonProperty("id")
	    private Integer id;

	    @JsonProperty("name")
	    private String name;

	    @JsonProperty("description")
	    private String description;

	    @JsonProperty("archive")
	    private byte[] archive;
	    
	    
    public Version() {
        // Конструктор по умолчанию
    }

    // Геттеры и сеттеры

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public byte[] getArchive() {
        return archive;
    }

    public void setArchive(byte[] archive) {
        this.archive = archive;
    }
}
