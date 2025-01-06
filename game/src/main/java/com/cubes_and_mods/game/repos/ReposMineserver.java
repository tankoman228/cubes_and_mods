package com.cubes_and_mods.game.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.game.db.Mineserver;

import jakarta.transaction.Transactional;

@Repository
public interface ReposMineserver extends JpaRepository<Mineserver, Integer> {

	@Query("SELECT m FROM Mineserver m WHERE m.idMachine = :id")
	List<Mineserver> findByIdOfMachine(@Param("id") int id);
	
    @Modifying
    @Query("UPDATE Mineserver m SET m.secondsWorking = m.secondsWorking + :s WHERE m.id = :id")
    void addSeconds(@Param("id") Integer id, @Param("s") Integer s);
}
