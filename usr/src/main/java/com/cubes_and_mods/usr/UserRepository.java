package com.cubes_and_mods.usr;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cubes_and_mods.usr.db.User;

public interface UserRepository extends JpaRepository<User, Long> {
}