package com.cubes_and_mods.game.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.game.db.Tariff;

@Repository
public interface ReposTariff extends JpaRepository<Tariff, Integer> {

}
