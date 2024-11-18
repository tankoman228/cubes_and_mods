package service_repos;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

	// TODO: change base path to another better one
	public static final String PATH_TO_BACKUPS = "/home/tank/mine_backups/";

	
	@Autowired
	private ReposBackup reposBackup;
	
	@Autowired
	private ReposBackupDestination reposBackupDestination;
	
	@Autowired
	private ReposMineserver reposMineserver;
	
	@Autowired
	private ReposMachine reposMachine;
	
	private volatile Map<Integer, String> taskStatusMap = new HashMap<>();
	
	
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
			var temp_path = PATH_TO_BACKUPS + handler.getMineserver().getId() + "/" + b_name;

			ArchivesAndFilesManager.Archivate(dirToArchivate, temp_path);
			File tmp = new File(temp_path);
			taskStatusMap.put(TASK_ID, "Archive created");
			
			var mineserver = handler.getMineserver();
			
			int success = 0;
			var destinations = reposBackupDestination.findAll();
			for (var d: destinations) {
				if (d.getIdMineserver() == mineserver.getId()) {
					
					taskStatusMap.put(TASK_ID, "Sending to " + d.getIdMachine());
					
					var copy_address = reposMachine.findById(d.getIdMachine()).get().getAddress() +
							"/upload/" + temp_path;
					try {
						ArchivesAndFilesManager.SendFileByParts(tmp, copy_address);
						success++;
					} catch (Exception e) {e.printStackTrace();}
				}
			}
			
			if (success == 0) {
				throw new Exception("No selected backup targets");
			}
			
			taskStatusMap.put(TASK_ID, "Saved, saving info to database");
			
			var b = new Backup();
		
			b.setName(b_name);
			b.setSizeKb((int)tmp.getTotalSpace() / 1024);
			b.setIdMineserver(mineserver.getId());
		
			reposBackup.save(b);
			
			taskStatusMap.put(TASK_ID, "Delete tmp archive file");
			tmp.delete();
			
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
	public void SaveBackupArchive(byte[] archive, String path) {
		
		if (archive == null) {
			System.out.println("------ END OF TRANSMIT ------");
			return;
		}
		
		ArchivesAndFilesManager.GetFileByParts(path, archive);
	}
	
	/**
	 * invoked a lot of times until EndOfTransmit becomes true
	 * Saves a new archive of backup chunk by chunk of size FILE_CHUNK_SIZE
	 * At the end we get in PATH_TO_BACKUPS a new archive
	 * */
	@Async
	public void RollbackBackupArchive(IMinecraftHandler handler, long id_backup, int TASK_ID) {
		
		taskStatusMap.put(TASK_ID, "Started");
		
		try {
			
			handler.killProcess();
			File rootToReplace = handler.GetFilesTree();
			
			var b = reposBackup.findById(id_backup).get();
			
			// TODO: what to do? I don't understand what to do and how to implement this
			int success = 0;
			var destinations = reposBackupDestination.findAll();
			for (var d: destinations) {
				if (d.getIdMineserver() == b.getIdMineserver()) {
					
					taskStatusMap.put(TASK_ID, "Sending to " + d.getIdMachine());
					
					var temp_path = PATH_TO_BACKUPS + handler.getMineserver().getId() + "/" + b.getName();
					var address = reposMachine.findById(d.getIdMachine()).get().getAddress() +
							"/require/" + temp_path;
					
					try {
						
						
						while (true) {
							break; 
						}
						
						success++;
					} catch (Exception e) {e.printStackTrace();}
				}
			}
			
			if (success == 0) {
				throw new Exception("No selected backup targets");
			}
						
			taskStatusMap.put(TASK_ID, "Finished");
		}
		catch (Exception e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Exit with error: " + e.getMessage());
		}
		
		
		// TODO: implement this function further, get from first found backup destination
	}
	
	@Async
	public void RemoveBackupArchive(IMinecraftHandler handler, Backup backup, int TASK_ID) {
		// TODO: implement
		
	}
	
	
	public void RequireArchive(String path, int id_op) {
		// TODO: implement
		//ArchivesAndFilesManager.SendFileByParts(path, );
	}
	
	public void GetBackupArchive(String path, byte[] bytes) {
		ArchivesAndFilesManager.GetFileByParts(path, bytes);
	}
}
