package service_repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cubes_and_mods.game.db.BackupDestination;

@Repository
public interface ReposBackupDestination extends JpaRepository<BackupDestination, Long> {
}
