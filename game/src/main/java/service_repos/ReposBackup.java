package service_repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.game.db.Backup;

import jakarta.transaction.Transactional;

@Repository
public interface ReposBackup extends JpaRepository<Backup, Long>  {
	
	@Modifying
	@Transactional
	@Query("SELECT m FROM Backup m WHERE m.idMineserver = :id")
	List<Backup> findByIdOfMineserver(@Param("id") int id);
}
