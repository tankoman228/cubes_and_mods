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
        initBashSession();
        executor.submit(() -> {
            sendBashCommand("tail -n 30 -F /tmp/server.log");
            try {
                while (running) {
                    String line = outputQueue.take(); // блокирует, пока не появится строка
                    consumer.accept(line);
                }
            } catch (InterruptedException e) {
                consumer.accept("Ошибка: поток прерван");
                Thread.currentThread().interrupt();
            }
        });
    }

    public void input(String input) {
        // Костыль, но иначе я ХЗ, как туда войти
        try {
            String fifoPath = "/tmp/" + sessionName + ".pipe";
            new ProcessBuilder(
                "docker", "exec", "-i", containerName,
                "bash", "-c", "echo " + shellEscape(input) + " > " + fifoPath
            ).start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private String shellEscape(String input) {
        if (input == null) return "''";
        return "'" + input.replace("'", "'\"'\"'") + "'";
    }

    public void killGameServer() {
        initBashSession();
        sendBashCommand("lsof -t -i:25565 -i:25566 | xargs -r kill -9");
    }

    public void startGameServer() {
        if (isGameServerAlive()) {
            return;
        }
        initBashSession();

        System.out.println("Убиваем сессию, только если она существует");
        sendBashCommand("tmux has-session -t " + sessionName + " 2>/dev/null && tmux kill-session -t " + sessionName);

        // Небольшая пауза, чтобы tmux точно освободил имя
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Создаём FIFO-файл");
        sendBashCommand("rm -f /tmp/" + sessionName + ".pipe; mkfifo /tmp/" + sessionName + ".pipe");
        System.out.println("Запускаем новую сессию tmux с tail -f");
        sendBashCommand(
            "tmux new-session -d -s " + sessionName +
            " \"sh -lc 'cd /game && tail -f /tmp/" + sessionName + ".pipe | stdbuf -oL -eL sh run.sh >> /tmp/server.log 2>&1'\""
        );

        System.out.println("Game server started");
    }

    private void sendBashCommand(String command) {
        System.out.println("Sending command to " + this.containerName + ": " + command);
        if (processWriter == null) initBashSession();
        processWriter.println(command);
        processWriter.flush();
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
