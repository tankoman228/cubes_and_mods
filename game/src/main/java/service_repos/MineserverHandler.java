package service_repos;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.cubes_and_mods.game.db.Version;


/**
 * Handles minecraft server process, helps to launch, and control this process
 * */
public class MineserverHandler {

    private int id_mine; // ID of minecraft server
    private PrintWriter processWriter;
    private Process process;
    private String serverDirectory; // Directory for this Minecraft server instance

    public static String BASE_PATH_FOR_SERVERS;

    public MineserverHandler(int id_mineserver) {
        id_mine = id_mineserver;
        serverDirectory = BASE_PATH_FOR_SERVERS + "/server_" + id_mine;
    }

    /** 
     * Inits server by unpacking archive from database
     * */
    public void initializeByVersion(Version version) {
        // TODO: need a real implementation
    }

    /**
     * Launches minecraft server, connects to its process NOT IMPLENETED
     * */
    public void launch() {
    	// TODO: need a real implementation
        if (!isLaunched()) {
            try {
                ProcessBuilder builder = new ProcessBuilder("java", "-Xmx1024M", "-Xms1024M", "-jar", "server.jar", "nogui");
                builder.directory(new File(serverDirectory));
                process = builder.start();
                processWriter = new PrintWriter(process.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace(); // Handle it appropriately
            }
        }
    }

    /**
     * Is game server opened? Is it working?
     * */
    public boolean isLaunched() {
        return process != null && process.isAlive();
    }

    /**
     * Kill game process. Like CTRL+C in console, avoid using when you can print "stop" to console 
     * */
    public void killProcess() {
        if (isLaunched()) {
            process.destroy(); // or process.destroyForcibly() if required
        }
    }

    /**
     * Send command to server like printing it to console 
     * */
    public String sendMessage(String message) {
        if (processWriter != null) {
            processWriter.println(message);
            return "Message sent: " + message;
        } else {
            return "Server not launched.";
        }
    }

    /**
     * Returns the previous output log NOT IMPLENETED
     * */
    public String getOutputHistory() {
        // TODO: need real implemetation
        File logFile = new File(serverDirectory, "logs/latest.log");
        try {
            return new String(Files.readAllBytes(logFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading log file.";
        }
    }

    /**
     * Returns all logs NOT IMPLENETED
     * */
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
    public File GetFilesTree() {
        return new File(serverDirectory);
    }

    /**
     * Returns file from directory of minecraft server NOT IMPLENETED
     * */
    public File GetFile(String path) {
        File file = new File(serverDirectory, path);
        return file.exists() ? file : null;
    }

    /**
     * Create new file in minecraft server
     * */ 
    public void CreateFile(String path, byte[] contents) {
        try {
            Files.write(Paths.get(serverDirectory, path), contents);
        } catch (IOException e) {
            e.printStackTrace(); // Handle it
        }
    }
    
    /**
     * Deleting file from server directory
     * */
    public void DeleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(serverDirectory, path));
        } catch (IOException e) {
            e.printStackTrace(); // Handle it
        }
    }
}
