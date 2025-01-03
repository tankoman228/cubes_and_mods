package com.cubes_and_mods.web.Clients.model;

public class VersionWithoutArchive {
	
    public Integer id;
    public String name;
    public String description;

    public VersionWithoutArchive(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
