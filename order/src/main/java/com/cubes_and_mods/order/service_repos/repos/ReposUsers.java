package com.cubes_and_mods.order.service_repos.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.order.db.User;

@Repository
public interface ReposUsers extends JpaRepository<User, Integer>  {}
