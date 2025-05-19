package com.cubes_and_mods.host.jpa.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.host.jpa.*;

@Repository
public interface ServerRepos extends JpaRepository<Server, Integer> {}