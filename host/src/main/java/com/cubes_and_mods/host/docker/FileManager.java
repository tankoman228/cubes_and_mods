package com.cubes_and_mods.host.docker;

import com.cubes_and_mods.host.jpa.Backup;
import com.cubes_and_mods.host.jpa.Host;
import com.cubes_and_mods.host.jpa.Version;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Всё, что связано с файлами внутри контейнера для игрового сервера
 */
public class FileManager {
    private final DockerClient client;
    private final Host host;
    private final String containerName;

    public FileManager(DockerClient client, Host host) {
        this.client = client;
        this.host = host;
        this.containerName = "mc-container-" + host.getId();
    }

    public boolean isGameServerInstalled() {
        // check presence of /game/run.sh
        ExecCreateCmdResponse resp = client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "test -e /game/run.sh && echo ok")
            .withAttachStdout(true)
            .exec();
        // read response...
        return false;
    }

    public void initGameServerByVersion(Version version) {
        // upload zip bytes to /backup/version.zip then unzip to /game
        FileInfo zip = new FileInfo();
        zip.path = "/backup/version-" + Instant.now().toEpochMilli() + ".zip";
        zip.contents = version.getArchive();

        System.out.println("Uploading file to " + zip.path);
        uploadFile(zip);

        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "unzip -o " + zip.path + " -d /game")
            .exec();

        System.out.println("Init game server succeess");
    }


    public FileInfo getFileTree() {
        // Use docker exec to list files recursively
        String cmd = "find /game -printf '%P|%y|%s\n'";
        // Parse output into FileInfo tree
        // ... implementation omitted for brevity
        return null;
    }

    public FileInfo getFileContents(String file) {
        try (InputStream is = client.copyArchiveFromContainerCmd(containerName, file).exec()) {
            // If file is directory, we get tar; else file contents
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            FileInfo info = new FileInfo();
            info.path = file;
            info.contents = os.toByteArray();
            info.isDirectory = false;
            info.size = (long) info.contents.length;
            return info;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String file) {
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "rm -rf " + file)
            .exec();
    }

    public void uploadFile(FileInfo file) {
        // Write temp archive
        try {
            Path tmp = Files.createTempFile("upload", ".tar");
            try (TarArchiveOutputStream tos = new TarArchiveOutputStream(Files.newOutputStream(tmp))) {
                TarArchiveEntry entry = new TarArchiveEntry(file.path);
                entry.setSize(file.contents.length);
                tos.putArchiveEntry(entry);
                tos.write(file.contents);
                tos.closeArchiveEntry();
            }
            try (InputStream is = Files.newInputStream(tmp)) {
                client.copyArchiveToContainerCmd(containerName)
                    .withTarInputStream(is)
                    .withRemotePath("/")
                    .exec();
            }
            Files.delete(tmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeBackup(Backup backup) {
        String archive = "/backup/" + backup.getName() + "-" + backup.getCreatedAt().toString() + ".zip";
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "cd /game && zip -r " + archive + " .")
            .exec();
    }

    public void deleteBackup(Backup backup) {
        String pattern = "/backup/" + backup.getName() + "*.zip";
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "rm -f " + pattern)
            .exec();
    }

    public void rollbackToBackup(Backup backup) {
        String archive = "/backup/" + backup.getName() + "-" + backup.getCreatedAt().toString() + ".zip";
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "rm -rf /game/* && unzip -o " + archive + " -d /game")
            .exec();
    }
}
