package com.cubes_and_mods.buy.service_repos.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.buy.db.Machine;

@Repository
public interface ReposMachines extends JpaRepository<Machine, Integer>  {

}
