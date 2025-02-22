package com.cubes_and_mods.host.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//import com.cubes_and_mods.host.db.Backup;
//import com.cubes_and_mods.host.repos.ReposBackup;
import com.cubes_and_mods.host.service.mineserver_process.IMinecraftHandler;

/**
 * Создание бекапов игры на локальном диске.
 * Вся логика распаковки/запаковки в архивы, управление ими.
 * Запись в БД данных о бекапах
 * */
@Service
public class ServiceBackup {

	//@Autowired
	//private ReposBackup reposBackup;
	
	// Статусы асинхронных задач 
	private volatile ConcurrentHashMap<Integer, String> taskStatusMap = new ConcurrentHashMap<>();
	
	
	//public List<Backup> GetBackupsForMineserver(int id) {
	//	return reposBackup.findByIdOfMineserver(id);
	//}
	
	public String getStatus(Integer id) {
		return taskStatusMap.getOrDefault(id, "Неизвестный код задачи"); //Unknown task code
	}
	
	/**
	 * Creates a new Backup, it's files and INSERT to DB
	 * */
	@Async
	public void CreateBackup(IMinecraftHandler handler, String b_name, int TASK_ID) {
		/*
		taskStatusMap.put(TASK_ID, "Старт"); //Start
		
		try {
			var mineserver = handler.getMineserver();			
			
			File dirToArchivate = handler.GetFilesTree();
			var path = getPathOfBackup(handler, b_name);

			taskStatusMap.put(TASK_ID, "Создание архива..."); //Making zip
			ArchivesAndFilesManager.Archivate(dirToArchivate, path);
			File archive = new File(path);
			taskStatusMap.put(TASK_ID, "Архив создан");//Archive created
			
			// Save to DB
			var b = new Backup();	
			b.setName(b_name);

			b.setSizeKb((archive.length() / (long)1024));

			b.setIdMineserver(mineserver.getId());
			b.setCreatedAt(LocalDateTime.now());	
			
			taskStatusMap.put(TASK_ID, "Сохранение в базу данных...");//Saving info to database
			reposBackup.save(b);

			taskStatusMap.put(TASK_ID, "Готово!");//Finished
		}
		catch (Exception e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Завершено на этапе " + taskStatusMap.get(TASK_ID) + " с ошибкой: " + e.getMessage());
			//Exit on stage
			// with error: 
		}*/
	}
	
	
	/**
	 * invoked a lot of times until EndOfTransmit becomes true
	 * Saves a new archive of backup chunk by chunk of size FILE_CHUNK_SIZE
	 * At the end we get in PATH_TO_BACKUPS a new archive
	 * */
	@Async
	public void RollbackBackupArchive(IMinecraftHandler handler, long id_backup, int TASK_ID) {
		/*
		taskStatusMap.put(TASK_ID, "Старт"); //Started
		
		try {		
			handler.killProcess();
			
			var b = reposBackup.findById(id_backup).get();

			try {
				File rootToReplace = handler.GetFilesTree();
				
				taskStatusMap.put(TASK_ID, "Распаковка..."); //Dearchivation
				ArchivesAndFilesManager.DeArchivate(
						rootToReplace, 
						getPathOfBackup(handler.getMineserver().getId(), b.getName()));
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
									
			taskStatusMap.put(TASK_ID, "Готово!"); //Finished
		}
		catch (Exception e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Завершено на этапе: " + e.getMessage()); //Exit with error: 
		}*/
	}
	
	/**
	 * Delete both files and info in DB about it
	 * */
	/*
	@Async
	public void DeleteBackup(IMinecraftHandler handler, Backup backup, int TASK_ID) {
		
		taskStatusMap.put(TASK_ID, "Старт"); //Start
		var path = getPathOfBackup(handler, backup.getName());
		try {
			reposBackup.delete(backup);
			Files.delete(Path.of(path));
			
			taskStatusMap.put(TASK_ID, "Готово!"); //Success
		} catch (IOException e) {
			e.printStackTrace();
			taskStatusMap.put(TASK_ID, "Завершено на этапе: "); //Error
		}
	}*/
	
	// Paths in file system
	private String getPathOfBackup(IMinecraftHandler handler, String b_name) {
		return getPathOfBackup(-3, b_name);
	}
	private String getPathOfBackup(int mine_id, String b_name) {
		return Config.PATH_TO_BACKUPS + mine_id + "/" + b_name + ".zip";
	}
}
