package service_repos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.db.BackupDestination;

@Service
public class ServiceBackupDestinations {

	@Autowired
	ReposBackupDestination repos;
	
	public List<BackupDestination> getAllForMineserver (int id_mineserver) {
		
		List<BackupDestination> l = new ArrayList<>();
		for (var d: repos.findAll()) {
			if (d.getIdMineserver() == id_mineserver) {
				l.add(d);
			}
		}
		return l;
	}
	
	public void addDestination(BackupDestination dest) {
		// TODO: implement
	}
	
	public void removeDestination(BackupDestination dest) {
		// TODO: implement
	}
}
