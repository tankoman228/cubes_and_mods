package com.cubes_and_mods.res;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.cubes_and_mods.res.db.Version;

import jakarta.transaction.Transactional;

public class ServerFilesManager {

    public static Process serverProcess;

    @Transactional
    public static Version uploadVersion(String name, String description, String directoryPath, String startCommand) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Provided path is not a valid directory.");
        }

        Version version = new Version();
        version.setName(name);
        version.setDescription(description);
        version.setArchive(zipDirectory(directory));
        version.setStartCommand(startCommand);

        return version;
    }

    private static byte[] zipDirectory(File directory) throws IOException {
        File tempZip = File.createTempFile("server", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip))) {
            zipFile(directory, directory.getName(), zos);
        }
        return Files.readAllBytes(tempZip.toPath());
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    public static void extractVersion(Version version, String outputPath) throws IOException {
        byte[] archive = version.getArchive();
        File zipFile = new File(outputPath + "/server.zip");
        
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(archive);
        }

        // Логика для распаковки архива
        unzip(zipFile, new File(outputPath));
    }
    
    private static void unzip(File zipFile, File destDir) throws IOException {
        // Создаем директорию назначения, если она не существует
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            
            // Обрабатываем каждый элемент в ZIP-файле
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());
                // Проверяем, является ли текущий элемент директорией
                if (zipEntry.isDirectory()) {
                    // Если это директория, создаем ее
                    if (!newFile.isDirectory()) {
                        newFile.mkdirs();
                    }
                } else {
                    // Если это файл, создаем все необходимые родительские директории
                    new File(newFile.getParent()).mkdirs();
                    
                    // Пишем содержимое файла
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) >= 0) {
                            bos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public static void startServer(String startCommand) throws IOException {
        // Убедитесь, что ваша команда верна и не требует `sudo`, если это возможно
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", startCommand);
        processBuilder.redirectErrorStream(true);
        serverProcess = processBuilder.start();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()))) {
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // Сохранение вывода в переменную.
            String processOutput = output.toString();
            System.out.println("Server Output: " + processOutput);
        }
    }

}