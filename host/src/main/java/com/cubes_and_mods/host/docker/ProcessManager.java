package com.cubes_and_mods.host.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import com.cubes_and_mods.host.jpa.Host;

public class ProcessManager {
    
    private final String containerName;
    private Process bashProcess;
    private PrintWriter processWriter;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = false;
    private final String sessionName = "gameserver";

    public ProcessManager(Host host) {
        this.containerName = "mc-container-" + host.getId();
        initBashSession();
    }

    public boolean isGameServerAlive() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", containerName, "tmux", "has-session", "-t", sessionName
            );
            Process proc = pb.start();
            int exitCode = proc.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void subscribeToGameserverConsoleOutput(Consumer<String> consumer) {
        executor.submit(() -> {
            // читаем лог, в котором падает вывод сервера
            sendBashCommand("tail -n 50 -f /tmp/server.log");
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
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("exec");
        command.add("-i");
        command.add(containerName);
        command.add("tmux");
        command.add("send-keys");
        command.add("-t");
        command.add(sessionName);
        command.add(input);         // <--- передаём всю строку как есть
        command.add("Enter");       // <--- и потом Enter
    
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    public void killGameServer() {
        // это аккуратно завершит tmux-сессию
        sendBashCommand("tmux kill-session -t " + sessionName);
    }

    public void startGameServer() {
        if (isGameServerAlive()) {
            return;
        }
        initBashSession();

        // Убиваем сессию, только если она существует
        sendBashCommand("tmux has-session -t " + sessionName + " 2>/dev/null && tmux kill-session -t " + sessionName);

        // Небольшая пауза, чтобы tmux точно освободил имя
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Запускаем новую сессию
        sendBashCommand("tmux new-session -d -s " + sessionName +
            " \"sh -lc 'cd /game && sh run.sh >> /tmp/server.log 2>&1'\""); 
    }

    private void sendBashCommand(String command) {
        if (processWriter == null) initBashSession();
        processWriter.println(command);
        processWriter.flush();
    }

    private String waitForOutput(int timeout, TimeUnit unit) throws TimeoutException {
        StringBuilder sb = new StringBuilder();
        long end = System.nanoTime() + unit.toNanos(timeout);
        while (System.nanoTime() < end) {
            String line = outputQueue.poll();
            if (line != null) sb.append(line).append("\n");
            else Thread.yield();
        }
        if (sb.length() == 0) throw new TimeoutException();
        return sb.toString();
    }

    private void handleError(String msg, Exception e) {
        e.printStackTrace();
        cleanup();
    }

    public void cleanup() {
        running = false;
        if (bashProcess != null) bashProcess.destroy();
        executor.shutdownNow();
    }

    private void initBashSession() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "-i", containerName, "/bin/bash", "-l"
            );
            bashProcess = pb.start();
            processWriter = new PrintWriter(bashProcess.getOutputStream(), true);
            running = true;
            executor.submit(this::readOutput);

            System.out.println(containerName + " bash session started");
        } catch (IOException e) {
            handleError("Failed to start bash session", e);
        }
    }

    private void readOutput() {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(bashProcess.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null && running) {
                outputQueue.put(line);
            }
        } catch (IOException | InterruptedException e) {
            handleError("Error reading output", e);
        }
    }
}
