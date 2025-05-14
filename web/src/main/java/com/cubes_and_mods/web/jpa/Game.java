package com.cubes_and_mods.web.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Game {

    private Integer id;

    @JsonProperty("name")
    private String name;

    public Game() {}
    
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
}
