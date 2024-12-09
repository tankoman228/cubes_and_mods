package com.cubes_and_mods.game.service.mineserver_process;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputFilter.Config;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.web.socket.WebSocketSession;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Version;


/**
 * Handles minecraft server process, helps to launch, and control this process
 * */
public class MinecraftHandler implements IMinecraftHandler {

    private Mineserver mine;
    private PrintWriter processWriter;
    private Process process;
    private String serverDirectory; // Directory for this Minecraft server instance
    private String start_command; 
    
    private List<ITextCallback> outputSubscribers = new CopyOnWriteArrayList<>();
    
    public static String BASE_PATH_FOR_SERVERS = "/home/tank/cubes_and_mods"; // TODO: change to config value

    public MinecraftHandler(Mineserver mineserver, String start_command) {
    	
       mine = mineserver;
       this.start_command = start_command;
       serverDirectory = BASE_PATH_FOR_SERVERS + "/server_" + mineserver.getId();
    }
    
    @Override
    public Mineserver getMineserver() { return mine;}

    /** 
     * Inits server by unpacking archive from database
     * */
    @Override
	public void initializeByVersion(Version version) throws FileNotFoundException, IOException {

    	byte[] archive = version.getArchive();
        File zipFile = new File(serverDirectory + "/server.zip");
        
        zipFile.getParentFile().mkdirs();
        
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(archive);
        }
        
        unzip(zipFile, new File(serverDirectory));
    }

    /**
     * Launches minecraft server, connects to its process 
     * */
    @Override
    public String launch() throws IOException {
    	
    	if (this.isLaunched())
    		return "idiot";
    	
    	System.out.println("Mineserver launched!");
    	
    	try {
    	    File serverDirectory = new File(BASE_PATH_FOR_SERVERS + "/server_" + mine.getId());
    	    
    	    ProcessBuilder processBuilder = new ProcessBuilder("sh", "run.sh");
    	    processBuilder.directory(serverDirectory); 
    	    processBuilder.redirectErrorStream(true);
    	    
    	    process = processBuilder.start();
    	    processWriter = new PrintWriter(process.getOutputStream(), true);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return "error";
    	}

        new Thread(() -> {
        	
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            	
            	String line;
            	while ((line = reader.readLine()) != null) {
            		 try {
	            		for (ITextCallback subscriber : outputSubscribers) {          			
	                   
	                            subscriber.Callback(line);
	                        } 
	                    }
            		 catch (Exception e) {
                         System.out.println("SUBSCRIBE ERROR: ");
                         //e.printStackTrace();
                         //outputSubscribers.remove(subscriber);
                     }
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        	
        }).start();
        
        return "ППЛГОНД"; 
    }


    /**
     * Is game server opened? Is it working?
     * */
    @Override
	public boolean isLaunched() {
        return process != null && process.isAlive();
    }

    /**
     * Kill game process. Like CTRL+C in console, avoid using when you can print "stop" to console 
     * */
    @Override
	public void killProcess() {
    	
        if (isLaunched()) {
            process.destroy(); // or process.destroyForcibly() if required
        }
    }

    /**
     * Подписаться на вывод консоли сервера и передавать его через веб-сокет.
     * Метод должен возвращать поток, который будет передавать данные в реальном времени.
     * */
    @Override
    public boolean trySubscribeToConsoleOutput(ITextCallback session) {

    	if (!isLaunched()) return false;
    	
    	outputSubscribers.add(session);
    	
        return true;
    }
    
    /**
     * Send command to server like printing it to console 
     * */
    @Override
	public String sendMessage(String message) {
    	
        if (processWriter != null) {
        	try {
        		processWriter.println(message);
        		processWriter.flush();
                return "$ " + message;
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        		return "FATAL ERROR" ;
        	}
        } else {
            return "Server not launched.";
        }
    }

    /**
     * Returns the previous output log
     * */
    @Override
	public String getOutputHistory() {
    	
        File logFile = new File(serverDirectory, "logs/latest.log");
        try {
            return new String(Files.readAllBytes(logFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading log file.";
        }
    }

    /**
     * Returns all logs
     * */
    @Override
	public String[] getAllLogs() {
    	
        File logDir = new File(serverDirectory, "logs");
        List<String> logs = new ArrayList<>();    
        if (logDir.exists() && logDir.isDirectory()) {
            File[] logFiles = logDir.listFiles();
            if (logFiles != null) {
                for (File log : logFiles) {
                    if (log.getName().endsWith(".log")) {
                        logs.add(log.getName());
                    }
                }
            }
        }
        return logs.toArray(new String[0]);
    }

    /**
     * Returns tree of all files in minecraft server directory 
     * */
    @Override
	public File GetFilesTree() {
        return new File(serverDirectory);
    }

    /**
     * Returns file from directory of minecraft server NOT IMPLENETED
     * */
    @Override
	public File GetFile(String path) {
    	
    	
    	
        File file = new File(serverDirectory, path);
        return file.exists() ? file : null;
    }

    /**
     * Create new file in minecraft server
     * */ 
    @Override
	public void CreateFile(String path, byte[] contents) {
    	
    	Path filePath = Paths.get(serverDirectory, path);
        try {
            //Files.createDirectories(filePath.getParent());
            Files.write(filePath, contents);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    
    /**
     * Create new directory in minecraft server
     * */ 
    @Override
	public void CreateDirIfNotExists(String path) {
        try {
        	if (!Files.exists(Paths.get(serverDirectory, path))) {
                Files.createDirectory(Paths.get(serverDirectory, path));
        	}
        } catch (IOException e) {
            e.printStackTrace(); // Handle it
        }
    }
    
    /**
     * Deleting file from server directory
     * */
    @Override
	public void DeleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(serverDirectory, path));
        } catch (IOException e) {
            e.printStackTrace(); // Handle it
        }
    }
    
    /**
     * Unzip archive (of version) to directory
     * */
    private void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // Используем список для хранения имен файлов, чтобы переместить их позже
        List<File> filesToMove = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;

            // Первая итерация для нахождения начальной директории
            if ((zipEntry = zis.getNextEntry()) != null) {
                String firstEntryName = zipEntry.getName();
                String baseDir = firstEntryName.contains("/") ? firstEntryName.substring(0, firstEntryName.indexOf('/')) : firstEntryName;

                // Обрабатываем все записи в архиве
                do {
                    // Состояние переменной newFile определяет, где размещать файлы
                    File newFile = new File(destDir, zipEntry.getName().replaceFirst(baseDir + "/", ""));

                    if (zipEntry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        new File(newFile.getParent()).mkdirs();
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zis.read(buffer)) >= 0) {
                                bos.write(buffer, 0, length);
                            }
                        }
                    }
                } while ((zipEntry = zis.getNextEntry()) != null);
            }
        }

        // Удаляем временный архив
        if (!zipFile.delete()) {
            System.err.println("Не удалось удалить временный архив: " + zipFile.getPath());
        }
    }


}
