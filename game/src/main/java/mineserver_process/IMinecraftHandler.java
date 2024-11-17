package mineserver_process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.socket.WebSocketSession;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Version;

public interface IMinecraftHandler {

	
	Mineserver getMineserver();
	
	/** 
	 * Inits server by unpacking archive from database
	 * */
	void initializeByVersion(Version version) throws FileNotFoundException, IOException;

	/**
	 * Launches minecraft server, connects to its process 
	 * */
	String launch() throws IOException;

	/**
	 * Is game server opened? Is it working?
	 * */
	boolean isLaunched();

	/**
	 * Kill game process. Like CTRL+C in console, avoid using when you can print "stop" to console 
	 * */
	void killProcess();

	/**
	 * Подписаться на вывод консоли сервера и передавать его через веб-сокет.
	 * Метод должен возвращать поток, который будет передавать данные в реальном времени.
	 * */
	boolean trySubscribeToConsoleOutput(ITextCallback session);
	
	/**
	 * Send command to server like printing it to console 
	 * */
	String sendMessage(String message);

	/**
	 * Returns the previous output log
	 * */
	String getOutputHistory();

	/**
	 * Returns all logs
	 * */
	String[] getAllLogs();

	/**
	 * Returns tree of all files in minecraft server directory 
	 * */
	File GetFilesTree();

	/**
	 * Returns file from directory of minecraft server
	 * */
	File GetFile(String path);

	/**
	 * Create new file in minecraft server
	 * */
	void CreateFile(String path, byte[] contents);

	/**
	 * Deleting file from server directory
	 * */
	void DeleteFile(String path);

	/**
	 * Create new directory in minecraft server
	 * */
	void CreateDirIfNotExists(String path);
	
}