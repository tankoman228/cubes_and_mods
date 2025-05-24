package com.cubes_and_mods.admin.controller;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VersionDto {
    private Integer id;
    private String name;
    private String description;
    private Integer idGame;
}