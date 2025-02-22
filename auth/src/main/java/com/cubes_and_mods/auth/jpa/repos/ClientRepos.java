package com.cubes_and_mods.auth.jpa.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.auth.jpa.*;

@Repository
public interface ClientRepos extends JpaRepository<Client, Integer> {}