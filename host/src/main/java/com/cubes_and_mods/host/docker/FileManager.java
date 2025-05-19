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


    /**
     * Проверяет установку игрового сервера по наличию /game/run.sh
     */
    public boolean isGameServerInstalled() {
        try {
            // Выполняем команду в контейнере через docker exec
            String cmd = String.format(
                "docker exec %s bash -c 'test -e /game/run.sh && echo ok || echo fail'",
                containerName);
            String output = execCommand("bash", "-c", cmd);
            return output.trim().contains("ok");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Загружает zip-архив из Version в контейнер и распаковывает в /game
     */
    public void initGameServerByVersion(Version version) {
        String zipPath = "/version-" + Instant.now().toEpochMilli() + ".zip";
        Path tmpZip = null;
        try {
            // Создаем временный zip-файл
            tmpZip = Files.createTempFile("version-", ".zip");
            Files.write(tmpZip, version.getArchive());

            // Создаем директорию в контейнере
            execCommand("docker", "exec", containerName, "bash", "-c", "mkdir -p /backup");

            // Копируем zip-файл в контейнер
            execCommand("docker", "cp", tmpZip.toString(), containerName + ":" + zipPath);

            // Удаляем локальный временный файл
            Files.deleteIfExists(tmpZip);
            tmpZip = null;

            // Распаковываем архив внутри контейнера
            String unzipCmd = String.format(
                "docker exec %s bash -c 'unzip -o %s -d /game'",
                containerName, zipPath);
            String result = execCommand("bash", "-c", unzipCmd);
            System.out.println(result);
            System.out.println("Init game server success");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to init game server: " + e.getMessage(), e);
        } finally {
            if (tmpZip != null) {
                try { Files.deleteIfExists(tmpZip); } catch (IOException ignore) {}
            }
        }
    }

    /**
     * Удаляет все файлы игрового сервера из /game
     */
    public void uninstallGameServer() {
        try {
            String cmd = String.format(
                "docker exec %s bash -c 'rm -rf /game/*'",
                containerName);
            String result = execCommand("bash", "-c", cmd);
            System.out.println("Uninstall result: " + result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to uninstall game server: " + e.getMessage(), e);
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
        String src = containerName + ":/game/" + file;
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
        execCommand("docker", "exec", containerName, "bash", "-c", "rm -rf " + "game/" + file);
    }

    public void uploadFile(FileInfo file) throws IOException, InterruptedException {
        // Создаем TAR локально
        System.out.println("Создаем TAR локально");
        Path tmp = Files.createTempFile("upload", ".tar");
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(Files.newOutputStream(tmp))) {
            TarArchiveEntry entry = new TarArchiveEntry(file.path);
            entry.setSize(file.contents.length);
            tos.putArchiveEntry(entry);
            tos.write(file.contents);
            tos.closeArchiveEntry();
        }
        // Копируем в контейнер
        System.out.println("Копируем в контейнер");
        execCommand("docker", "cp", tmp.toString(), containerName  + ":/game/"
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
