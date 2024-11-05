package com.cubes_and_mods.res.service_repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.res.db.Machine;

/**
 * No comments yet
 */
@Repository
public interface ReposMachines extends JpaRepository<Machine, Integer> {}
