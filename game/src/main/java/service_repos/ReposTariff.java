package service_repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cubes_and_mods.game.db.Tariff;

import jakarta.transaction.Transactional;

public interface ReposTariff extends JpaRepository<Tariff, Integer> {

}
