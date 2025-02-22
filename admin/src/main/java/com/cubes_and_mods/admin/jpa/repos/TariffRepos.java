package com.cubes_and_mods.admin.jpa.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.admin.jpa.*;

@Repository
public interface TariffRepos extends JpaRepository<Tariff, Integer> {}