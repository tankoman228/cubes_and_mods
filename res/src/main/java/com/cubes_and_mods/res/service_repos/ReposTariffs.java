package com.cubes_and_mods.res.service_repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.res.db.Mineserver;
import com.cubes_and_mods.res.db.Tariff;

import jakarta.transaction.Transactional;

@Repository
public interface ReposTariffs extends JpaRepository<Tariff, Integer>  {
}
