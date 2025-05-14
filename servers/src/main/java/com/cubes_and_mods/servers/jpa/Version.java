package com.cubes_and_mods.servers.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
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
    //@JsonProperty("archive")
	@JsonIgnore
    private byte[] archive; // Это же та хрень из DTO, гигантская, шо капец

    @Column(name = "id_game", nullable = false)
    @JsonProperty("id_game")
    private Integer idGame;
}
