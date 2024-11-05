package com.cubes_and_mods.res.service_repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.res.db.Version;

import jakarta.transaction.Transactional;

@Repository
public interface ReposVersion extends JpaRepository<Version, Integer> {

	@Query("SELECT v.name FROM Version v")
	List<String> findAllVersionNames();

	@Modifying
	@Transactional
	@Query("DELETE FROM Version v WHERE v.name = :name")
	void deleteByName(@Param("name") String name);

}
