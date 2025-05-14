package com.cubes_and_mods.host.docker;

import com.cubes_and_mods.host.jpa.Backup;
import com.cubes_and_mods.host.jpa.Host;
import com.cubes_and_mods.host.jpa.Version;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Всё, что связано с файлами внутри контейнера для игрового сервера
 */
public class FileManager {

    private final DockerClient client;
    private final Host host;
    private final String containerName;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public FileManager(DockerClient client, Host host) {
        this.client = client;
        this.host = host;
        this.containerName = "mc-container-" + host.getId();
    }


    // Я не знаю как, но эти 2 метода работают и твкать их не стоит

    public boolean isGameServerInstalled() {
        try {
            // Создаём команду для проверки существования файла
            ExecCreateCmdResponse execCreateResponse = client.execCreateCmd(containerName)
                .withCmd("bash", "-c", "test -e /game/run.sh && echo ok || echo fail")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
    
            StringBuilder output = new StringBuilder();
    
            // Запускаем команду
            client.execStartCmd(execCreateResponse.getId())
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        String out = new String(frame.getPayload());
                        output.append(out);
                    }
                }).awaitCompletion();
    
            // Проверка вывода
            String result = output.toString().trim();
            return result.contains("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private volatile Boolean isFinished = false;
    public void initGameServerByVersion(Version version) throws InterruptedException {
        // upload zip bytes to /backup/version.zip then unzip to /game
        String zipPath = "/version-" + Instant.now().toEpochMilli() + ".zip";
    
        FileInfo zip = new FileInfo();
        zip.path = zipPath;
        zip.contents = version.getArchive();
    
        System.out.println("Uploading file to " + zip.path);
        try {
            uploadFile(zip);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file");
        }
    
        // Команда распаковки
        String unzipCmd = "unzip -o " + zipPath + " -d /game";
    
        // Создание exec-команды
        ExecCreateCmdResponse execCreateResponse = client.execCreateCmd(containerName)
            .withCmd("bash", "-c", unzipCmd)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .exec();

        // Запуск команды
        isFinished = false;
        client.execStartCmd(execCreateResponse.getId())
        .withDetach(false)
        .withTty(false)  // Иногда помогает тоже
        .exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame frame) {
                String output = new String(frame.getPayload());
                if (frame.getStreamType() == StreamType.STDOUT || frame.getStreamType() == StreamType.STDERR) {
                    System.out.print(output); // без println — чтобы не мешать выводу
                }
            }

            @Override
            public void onComplete() {
                System.out.println("\nUnzip completed.");
                isFinished = true;
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Unzip failed: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });

        // Ожидание завершения команды
        while (true) {  
            if (isFinished) {
                break;
            }
            Thread.sleep(3000); 
        }
    
        System.out.println("Init game server success");
    }


    // А вот тут уже да, нужно напрямую в терминал стучаться

    private String execCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            process.waitFor();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command failed (exit=" + exitCode + "): " + String.join(" ", command) + "\nOutput:\n" + sb);
            }

            return sb.toString().trim();
        }
    }

    public FileInfo getFileTree() throws IOException, InterruptedException {
        String findCmd = String.join(" ",
            "docker exec", containerName,
            "bash -c \"find /game -printf '%P|%y|%s\\n'\"");
        String output = execCommand("bash", "-c", findCmd);
        List<FileInfo> tree = new ArrayList<>();
        Map<String, FileInfo> map = new HashMap<>();
        for (String line : output.split("\\n")) {
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\|");
            String path = parts[0];
            String type = parts[1];
            long size = Long.parseLong(parts[2]);
            FileInfo info = new FileInfo();
            info.path = path;
            info.isDirectory = "d".equals(type);
            info.size = size;
            info.children = new ArrayList<>();
            map.put(path, info);
            if (path.contains("/")) {
                String parentPath = path.substring(0, path.lastIndexOf('/'));
                FileInfo parent = map.get(parentPath);
                if (parent != null) parent.children.add(info);
            } else {
                tree.add(info);
            }
        }

        FileInfo rootFileInfo = new FileInfo();
        rootFileInfo.path = "/";
        rootFileInfo.isDirectory = true;
        rootFileInfo.children = tree;
        return rootFileInfo;
    }

    public FileInfo getFileContents(String file) throws IOException, InterruptedException {
        // Copy file out via docker cp
        Path tmp = Files.createTempFile("file-", null);
        String src = containerName + ":/" + file;
        execCommand("docker", "cp", src, tmp.toString());
        byte[] data = Files.readAllBytes(tmp);
        Files.delete(tmp);
        FileInfo info = new FileInfo();
        info.path = file;
        info.contents = data;
        info.isDirectory = false;
        info.size = (long) data.length;
        return info;
    }

    public void deleteFile(String file) throws IOException, InterruptedException {
        execCommand("docker", "exec", containerName, "bash", "-c", "rm -rf " + file);
    }

    public void uploadFile(FileInfo file) throws IOException, InterruptedException {
        // Создаем TAR локально
        Path tmp = Files.createTempFile("upload", ".tar");
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(Files.newOutputStream(tmp))) {
            TarArchiveEntry entry = new TarArchiveEntry(file.path);
            entry.setSize(file.contents.length);
            tos.putArchiveEntry(entry);
            tos.write(file.contents);
            tos.closeArchiveEntry();
        }
        // Копируем в контейнер
        execCommand("docker", "cp", tmp.toString(), containerName + ":/"
        );
        Files.delete(tmp);
    }

    public long makeBackup(Backup backup) throws IOException, InterruptedException {
        String archive = getBackupArchiveName(backup);
        execCommand("docker", "exec", containerName, "bash", "-c", "mkdir -p /backup");
        execCommand("docker", "exec", containerName,
            "bash", "-c", "cd /game && zip -r " + archive + " .");
        String check = execCommand("docker", "exec", containerName,
            "bash", "-c",
            "if [ -f " + archive + " ]; then echo exists; else echo not_found; fi");
        if (!"exists".equals(check.trim())) {
            throw new RuntimeException("Backup archive verification failed: " + check);
        }
        String stat = execCommand("docker", "exec", containerName,
            "bash", "-c", "stat -c %s " + archive);
        long bytes = Long.parseLong(stat.trim());
        return bytes / 1024;
    }

    public void deleteBackup(Backup backup) throws IOException, InterruptedException {
        String archive = getBackupArchiveName(backup);
        execCommand("docker", "exec", containerName, "bash", "-c", "rm -f " + archive);
    }

    public void rollbackToBackup(Backup backup) throws IOException, InterruptedException {
        String archive = getBackupArchiveName(backup);
        execCommand("docker", "exec", containerName,
            "bash", "-c", "rm -rf /game/* && unzip -o " + archive + " -d /game");
    }

    public String getBackupArchiveName(Backup backup) {
        String sanitized = backup.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .format(backup.getCreatedAt());
        return "/backup/" + sanitized + "-" + timestamp + ".zip";
    }
    
}
