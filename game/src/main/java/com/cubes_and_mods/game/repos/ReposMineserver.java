package com.cubes_and_mods.game.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.game.db.Mineserver;

@Repository
public interface ReposMineserver extends JpaRepository<Mineserver, Integer> {

}
