package com.cubes_and_mods.host.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "backups")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "size_kb", nullable = false)
    @JsonProperty("size_kb")
    private Long sizeKb;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp DEFAULT now()")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(name = "name", length = 64)
    @JsonProperty("name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_host", nullable = false, referencedColumnName = "id")
    @JsonProperty("host")
    private Host hostBackup;
}
