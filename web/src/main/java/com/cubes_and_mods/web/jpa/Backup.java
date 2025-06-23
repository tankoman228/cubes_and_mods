package com.cubes_and_mods.web.jpa;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Backup {

    private Integer id;

    @JsonProperty("size_kb")
    private Long sizeKb;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("name")
    private String name;

    @JsonProperty("host")
    private Host hostBackup;
}
