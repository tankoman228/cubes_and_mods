package com.cubes_and_mods.auth.jpa;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "versions")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "description", nullable = false)
    @JsonProperty("description")
    private String description;

    @Column(name = "archive", nullable = false)
    @JsonProperty("archive")
    private byte[] archive;

    @Column(name = "id_game", nullable = false)
    @JsonProperty("id_game")
    private Integer idGame;

    public Version() {}
    
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

	public Integer getIdGame() {
		return idGame;
	}

	public void setIdGame(Integer idGame) {
		this.idGame = idGame;
	}
    
    
}
