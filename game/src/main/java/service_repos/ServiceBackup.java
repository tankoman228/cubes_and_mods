package service_repos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.db.Backup;

import mineserver_process.IMinecraftHandler;

@Service
public class ServiceBackup {


	public static final String PATH_TO_BACKUPS = "/home/tank/mine_backups/";
	public static final int FILE_CHUNK_SIZE = 8192;
	
	@Autowired
	private ReposBackup reposBackup;
	
	@Autowired
	private ReposBackupDestination reposBackupDestination;
	
	@Autowired
	private ReposMineserver reposMineserver;
	
	@Autowired
	private ReposMachine reposMachine;
	
	private Map<Integer, String> taskStatusMap = new HashMap<>();
	
	
	public List<Backup> GetBackupsForMineserver(int id) {
		return reposBackup.findByIdOfMineserver(id);
	}
	
	public String getStatus(Integer id) {
		return taskStatusMap.getOrDefault(id, "Unknown task code");
	}
	
	/**
	 * Creates a new Backup
	 * */
	@Async
	public void CreateBackup(IMinecraftHandler handler, String b_name, int TASK_ID) {
		
		taskStatusMap.put(TASK_ID, "Started");
		
		try {
			File dirToArchivate = handler.GetFilesTree();
			// TODO: Create archive and save to backup folder
			
			var mineserver = handler.getMineserver();
			
			var destinations = reposBackupDestination.findAll();
			for (var d: destinations) {
				if (d.getIdMineserver() == mineserver.getId()) {
					
					var copy_address = reposMachine.findById(d.getIdMachine()).get().getAddress();
					// TODO: Send HTTP queries for saving BackupArchive to copy_address, ignore if already saved
				}
			}
			
			var b = new Backup();
		
			b.setName(b_name);
			// TODO: b.setSizeKb(int ????);
			
			reposBackup.save(b);
			taskStatusMap.put(TASK_ID, "Finished");
		}
		catch (Exception e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Exit with error: " + e.getMessage());
		}
	}
	
	/**
	 * invoked a lot of times until EndOfTransmit becomes true
	 * Saves a new archive of backup chunk by chunk of size FILE_CHUNK_SIZE
	 * At the end we get in PATH_TO_BACKUPS a new archive
	 * */
	public void SaveBackupArchive(IMinecraftHandler handler, int id_backup, byte[] archive, boolean EndOfTransmit, int TASK_ID) {
		
		// TODO: implement this function
	}
	
	/**
	 * invoked a lot of times until EndOfTransmit becomes true
	 * Saves a new archive of backup chunk by chunk of size FILE_CHUNK_SIZE
	 * At the end we get in PATH_TO_BACKUPS a new archive
	 * */
	public void RollbackBackupArchive(IMinecraftHandler handler, int id_backup, int TASK_ID) {
		
		handler.killProcess();
		File rootToReplace = handler.GetFilesTree();
		
		// TODO: implement this function further, get from first found backup destination
	}
	
	public void RemoveBackupArchive(IMinecraftHandler handler, Backup backup, int TASK_ID) {
		// TODO: implement
	}
	

}
