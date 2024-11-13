package service_repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cubes_and_mods.game.db.Tariff;

public interface ReposTariff extends JpaRepository<Tariff, Integer> {

}
