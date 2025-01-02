package com.cubes_and_mods.game.service.mineserver_process;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.cubes_and_mods.game.db.Mineserver;
import com.cubes_and_mods.game.db.Tariff;
import com.cubes_and_mods.game.db.Version;
import com.cubes_and_mods.game.service.Config;


/**
 * Handles minecraft server process, helps to launch, and control this process, gives access to file system
 * */
public class MinecraftHandler implements IMinecraftHandler {

    private Mineserver mineserver;
    private PrintWriter processWriter;
    private Process process;
    private String serverDirectory; // Directory for this Minecraft server instance
    private Tariff tariff;
    
    private List<ITextCallback> outputSubscribers = new CopyOnWriteArrayList<>();

    public MinecraftHandler(Mineserver mineserver, Tariff tariff) {
    	
       this.mineserver = mineserver;
       this.tariff = tariff;
       
       serverDirectory = Config.PATH_TO_SERVERS + "/server_" + mineserver.getId();
    }
    
    @Override
    public Mineserver getMineserver() { return mineserver;}

    /** 
     * Inits server by unpacking archive from database
     * */
    @Override
	public void initializeByVersion(Version version) throws FileNotFoundException, IOException {

    	// Unpacking large BLOB
    	byte[] archive = version.getArchive();
        File zipFile = new File(serverDirectory + "/server.zip");
 
        zipFile.getParentFile().mkdirs();     
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(archive);
        }
        unzip(zipFile, new File(serverDirectory));
        
        // Запись параметров в user_jvm_args.txt
        short cpuThreads = tariff.getCpuThreads();
        short ramGb = tariff.getRam();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(serverDirectory + "/user_jvm_args.txt"))) {
            writer.write("-Xmx" + ramGb + "G");
            writer.newLine();
            writer.write("-XX:ActiveProcessorCount=" + cpuThreads);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Ошибка при записи в user_jvm_args.txt", e);
        }

        // Set valid data to server.properties       
        replaceProperty("max-players", tariff.getMaxPlayers());
        replaceProperty("server-port", (25564 + mineserver.getId()));
        replaceProperty("query.port", (25564 + mineserver.getId()));
    }
    
    /**
     * Replaces in server.properties property. Used for port and max-players
     * @throws IOException if can't change this property
     * */
    private void replaceProperty(String key, Object value) throws IOException {
    	
        String serverPropertiesPath = serverDirectory + "/server.properties";
        try {
            List<String> lines = Files.readAllLines(Paths.get(serverPropertiesPath));
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(key + "=")) {
                    lines.set(i, key + "=" + value);
                    break;
                }
            }
            Files.write(Paths.get(serverPropertiesPath), lines);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Ошибка при изменении server.properties", e);
        }
    }

    /**
     * Launches minecraft server, connects to its process 
     * */
    @Override
    public String launch() throws IOException {
    	
    	if (this.isLaunched())
    		return "already launched";
    	
    	System.out.println("Mineserver launching!");
    	
    	try {
    			
    		File serverDirectory = new File(Config.PATH_TO_SERVERS + "/server_" + mineserver.getId());

    		ProcessBuilder processBuilder;
    		if (System.getProperty("os.name").toLowerCase().contains("win")) {
    			System.out.println("I am the venda");
    		    processBuilder = new ProcessBuilder("cmd.exe","/c", "run.bat"); // Для Windows
    		} else {
    			System.out.println("I am linuxoid");
    		    processBuilder = new ProcessBuilder("sh", "run.sh"); // Для Linux и других UNIX систем
    		}

    		processBuilder.directory(serverDirectory);
    		processBuilder.redirectErrorStream(true);

    		process = processBuilder.start();
    		processWriter = new PrintWriter(process.getOutputStream(), true);
    		
        	System.out.println("Mineserver launched!");
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
    	
    	sendMessage("stop");
        try {
            process.destroyForcibly();
        } catch(Exception e) { 
        	e.printStackTrace(); 
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
    	
    	System.out.print("Delete " + path);
    	
        try {
        	RecursiveDelete(new File(path));
        } catch (Exception e) {
            e.printStackTrace(); // Handle it
            throw new RuntimeException("Cannot delete! " + e.getLocalizedMessage());
        }
    }
    
    private void RecursiveDelete(File file) {
    	
    	if (file.isDirectory()) {
    		for (var f: file.listFiles()) {			
    			 RecursiveDelete(f);
    		}
    	}
    	file.delete();
    }
    
    /**
     * Unzip archive (of version) to directory
     * */
    private void unzip(File zipFile, File destDir) throws IOException {
    	
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
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
