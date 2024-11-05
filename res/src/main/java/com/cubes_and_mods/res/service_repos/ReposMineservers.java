package com.cubes_and_mods.res.service_repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.res.db.Mineserver;

@Repository
public interface ReposMineservers extends JpaRepository<Mineserver, Integer> {

}
