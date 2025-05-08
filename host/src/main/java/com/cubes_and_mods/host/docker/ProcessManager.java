package com.cubes_and_mods.host.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import com.cubes_and_mods.host.jpa.Host;
import com.github.dockerjava.api.DockerClient;

// docker exec -i -t mc-container-2 /bin/bash вот так я запущу процесс для контроля контейнера
// А далее как обычный bash

/**
 * Управление процессом игрового сервера внутри контейнера при помощи exec и bash команд
 */
public class ProcessManager {
    
    private final String containerName;
    private Process bashProcess;
    private PrintWriter processWriter;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = false;
    private final String screenSessionName = "gameserver";

    public ProcessManager(Host host) {
        this.containerName = "mc-container-" + host.getId();
        initBashSession();
    }

    public boolean isGameServerAlive() {
        try {
            sendBashCommand("screen -ls");
            return waitForOutput(300, TimeUnit.MILLISECONDS).contains("game");
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void subscribeToGameserverConsoleOutput(Consumer<String> consumer) {
        executor.submit(() -> {
            sendBashCommand("screen -r " + screenSessionName);
            while (running) {
                try {
                    String line = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (line != null) consumer.accept(line);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void input(String input) {
        sendBashCommand(input);
    }

    public void killGameServer() {
        for (int i = 0; i < 3; i++) {
            sendControlChar(3); // Ctrl+C
        }
    }

    public void startGameServer() {
        sendBashCommand("screen -S " + screenSessionName + " -d -m");
        sendBashCommand("cd /game");
        sendBashCommand("sh run.sh");
    }

    private void sendBashCommand(String command) {

        if (processWriter == null) {
            initBashSession();
        }

        processWriter.println(command);
        processWriter.flush(); 
    }

    private void sendControlChar(int ascii) {
        sendBashCommand(new String(new char[] { (char) ascii }));
    }

    private String waitForOutput(int timeout, TimeUnit unit) throws TimeoutException {
        StringBuilder sb = new StringBuilder();
        long endTime = System.nanoTime() + unit.toNanos(timeout);
        
        while (System.nanoTime() < endTime) {
            String line = outputQueue.poll();
            if (line != null) sb.append(line).append("\n");
            else Thread.yield();
        }
        
        if (sb.length() == 0) throw new TimeoutException();
        return sb.toString();
    }

    private void handleError(String message, Exception e) {
        // Реализуйте логирование ошибок
        e.printStackTrace();
        cleanup();
    }

    public void cleanup() {
        running = false;
        if (bashProcess != null) bashProcess.destroy();
        if (executor != null) executor.shutdownNow();
    }

    private void initBashSession() {
        try {
            System.out.println("Starting bash session for container: " + containerName);

            ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "-i", containerName, "/bin/bash"
            );
            bashProcess = pb.start();
            processWriter = new PrintWriter(bashProcess.getOutputStream(), true);
            
            // Start output reader thread
            executor.submit(this::readOutput);
            running = true;

            System.out.println("Bash session started for container: " + containerName);
        } catch (IOException e) {
            handleError("Failed to start bash session", e);
        }
    }

    private void readOutput() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(bashProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null && running) {
                outputQueue.put(line);
                System.out.println("Received output: " + line);
            }
        } catch (IOException | InterruptedException e) {
            handleError("Error reading output", e);
        }
    }
}