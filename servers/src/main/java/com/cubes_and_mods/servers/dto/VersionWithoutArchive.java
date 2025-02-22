package com.cubes_and_mods.servers.dto;

/**
 * Used for getting versions while ignoring BLOBs. It is like db.Version, but without of archive field
 * */
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

