package com.cubes_and_mods.game.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

public class ArchivesAndFilesManager {

    public static final int ZIP_CHUNK_SIZE = 1024;

    /**
     * Makes archive of selected directory
     * */
    public static void Archivate(File baseDir, String path_archive) {
        try (FileOutputStream fos = new FileOutputStream(path_archive);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipDirectory(baseDir, baseDir.getName(), zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes archive of selected directory
     * */
    private static void zipDirectory(File dir, String baseName, ZipOutputStream zos) throws IOException {
    	
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, baseName + "/" + file.getName(), zos);
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(baseName + "/" + file.getName());
                        zos.putNextEntry(zipEntry);
                        byte[] buffer = new byte[ZIP_CHUNK_SIZE];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
    }

    
    public static void DeArchivate(File baseDir, String path_archive) {

        File archiveFile = new File(path_archive);
        if (!archiveFile.exists()) {
            System.err.println("Архив не найден: " + path_archive);
            return;
        }

        if (!baseDir.exists()) {
            baseDir.mkdirs();
        } else {
            if (baseDir.isDirectory() && baseDir.list().length > 0) {
                try {
                    Files.walk(baseDir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                } catch (IOException e) {
                    System.err.println("Ошибка при очистке директории: " + e.getMessage());
                    return;
                }
            }
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveFile))) {
        	
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(baseDir, zipEntry.getName());
                
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {

                    new File(newFile.getParent()).mkdirs();
                    
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                        byte[] buffer = new byte[ZIP_CHUNK_SIZE];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при разархивации: " + e.getMessage());
        }
    }
}
