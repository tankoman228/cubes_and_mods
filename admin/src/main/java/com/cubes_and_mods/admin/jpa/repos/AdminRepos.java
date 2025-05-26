package com.cubes_and_mods.admin.jpa.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.admin.jpa.*;

@Repository
public interface AdminRepos extends JpaRepository<Admin, Integer> {

    @Query("SELECT u FROM Admin u WHERE u.username = :email")
    Optional<Admin> findByEmail(@Param("email") String email);

}