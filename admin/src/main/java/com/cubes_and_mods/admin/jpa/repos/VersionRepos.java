package com.cubes_and_mods.admin.jpa.repos;
import com.cubes_and_mods.admin.jpa.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VersionRepos extends JpaRepository<Version, Integer> {}