package com.cubes_and_mods.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cubes_and_mods.auth.db.User;

public interface UserRepository extends JpaRepository<User, Long> {
}