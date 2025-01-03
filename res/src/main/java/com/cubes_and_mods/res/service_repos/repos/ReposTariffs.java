package com.cubes_and_mods.res.service_repos.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.res.db.Tariff;

@Repository
public interface ReposTariffs extends JpaRepository<Tariff, Integer>  {
}
