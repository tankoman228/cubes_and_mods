package com.cubes_and_mods.order.jpa.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.order.jpa.*;

@Repository
public interface VersionRepos extends JpaRepository<Version, Integer> {}