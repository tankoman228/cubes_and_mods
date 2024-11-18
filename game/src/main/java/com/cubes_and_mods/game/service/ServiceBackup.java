package com.cubes_and_mods.game.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.game.db.Backup;
import com.cubes_and_mods.game.repos.ReposBackup;
import com.cubes_and_mods.game.repos.ReposBackupDestination;
import com.cubes_and_mods.game.repos.ReposMachine;
import com.cubes_and_mods.game.repos.ReposMineserver;
import com.cubes_and_mods.game.service.mineserver_process.IMinecraftHandler;

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
			var temp_path = getPathOfBackup(handler, b_name);

			ArchivesAndFilesManager.Archivate(dirToArchivate, temp_path);
			File tmp = new File(temp_path);
			taskStatusMap.put(TASK_ID, "Archive created");
			
			var mineserver = handler.getMineserver();
			
			int success = 0;
			var destinations = reposBackupDestination.findAll();
			for (var d: destinations) {
				if (d.getIdMineserver() == mineserver.getId()) {
					
					taskStatusMap.put(TASK_ID, "Sending to " + d.getIdMachine());
					
					var copy_address = reposMachine.findById(d.getIdMachine()).get().getAddress();
					try {	
						var c = new FilesWebClient(copy_address + "/file");
						c.SendFile(temp_path, tmp);
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
			//tmp.delete();
			
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
	@Async
	public void RollbackBackupArchive(IMinecraftHandler handler, long id_backup, int TASK_ID) {
		
		taskStatusMap.put(TASK_ID, "Started");
		
		try {
			
			handler.killProcess();
			File rootToReplace = handler.GetFilesTree();
			
			var b = reposBackup.findById(id_backup).get();
			var path = getPathOfBackup(handler, b.getName());
			
			int success = 0;
			var destinations = reposBackupDestination.findAll();
			for (var d: destinations) {
				if (d.getIdMineserver() == b.getIdMineserver()) {
					
					taskStatusMap.put(TASK_ID, "Sending to " + d.getIdMachine());

					var address = reposMachine.findById(d.getIdMachine()).get().getAddress() + "/file";
					var s = new FilesWebClient(address);
					
					try {
						
						Files.createDirectories(Path.of(path));
						Files.createFile(Path.of(path));
						
						s.GetFile(path, new File(path));
						
						success++;
						break;
					} catch (Exception e) {e.printStackTrace();}
				}
			}
			
			if (success == 0) {
				throw new Exception("No selected backup targets");
			}
			
			taskStatusMap.put(TASK_ID, "Unpacking");
			
			ArchivesAndFilesManager.DeArchivate(rootToReplace, PATH_TO_BACKUPS);
						
			taskStatusMap.put(TASK_ID, "Finished");
		}
		catch (Exception e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Exit with error: " + e.getMessage());
		}
	}
	
	@Async
	public void RemoveBackupArchive(IMinecraftHandler handler, Backup backup, int TASK_ID) {
		
		var path = getPathOfBackup(handler, backup.getName());
		try {
			Files.delete(Path.of(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getPathOfBackup(IMinecraftHandler handler, String b_name) {
		return getPathOfBackup(handler.getMineserver().getId(), b_name);
	}
	private String getPathOfBackup(int mine_id, String b_name) {
		return PATH_TO_BACKUPS + mine_id + "/" + b_name;
	}

	public Boolean exists(long id) {
		
		var b = reposBackup.getReferenceById(id);
		return Files.exists(Path.of(getPathOfBackup(b.getIdMineserver(), b.getName())));
	}
}
