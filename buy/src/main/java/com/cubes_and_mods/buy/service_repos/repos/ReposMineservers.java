package com.cubes_and_mods.buy.service_repos.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.buy.db.Mineserver;

import jakarta.transaction.Transactional;

@Repository
public interface ReposMineservers extends JpaRepository<Mineserver, Integer> {
	
	@Modifying
	@Transactional
	@Query("SELECT m FROM Mineserver m WHERE m.idUser = :id")
	List<Mineserver> findByIdOfUser(@Param("id") int id);
	
	@Modifying
	@Transactional
	@Query("SELECT m FROM Mineserver m WHERE m.idMachine = :id")
	List<Mineserver> findByIdOfMachine(@Param("id") int id);
}
