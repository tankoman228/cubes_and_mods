package com.cubes_and_mods.servers.service_repos.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.servers.db.Machine;

/**
 * No comments yet
 */
@Repository
public interface ReposMachines extends JpaRepository<Machine, Integer> {}
