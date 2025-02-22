package com.cubes_and_mods.auth.db;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "versions")
public class Version {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @JsonProperty("id")
	    private Integer id;

	    @Column(name = "name", nullable = false)
	    @JsonProperty("name")
	    private String name;

	    @Column(name = "description", nullable = false, columnDefinition = "text")
	    @JsonProperty("description")
	    private String description;

	    @Column(name = "archive", nullable = false)
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
