package com.cubes_and_mods.admin.jpa.repos;
import com.cubes_and_mods.admin.controller.VersionDto;
import com.cubes_and_mods.admin.jpa.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VersionRepos extends JpaRepository<Version, Integer> {
        @Query("SELECT new com.cubes_and_mods.admin.controller.VersionDto(v.id, v.name, v.description, v.idGame) " +
           "FROM Version v " +
           "WHERE v.idGame = :gameId AND LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<VersionDto> findByGameIdAndNameContaining(@Param("gameId") Integer gameId, 
                                                   @Param("searchTerm") String searchTerm);
}