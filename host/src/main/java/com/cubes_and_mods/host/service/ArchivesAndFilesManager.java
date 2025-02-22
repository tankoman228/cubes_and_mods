package com.cubes_and_mods.host.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Static methods for creating or unpacking archives
 * 
 * Used in backup service
 * */
public class ArchivesAndFilesManager {

    public static final int ZIP_CHUNK_SIZE = 1024;

    /**
     * Makes archive of selected directory
     * */
    public static void Archivate(File baseDir, String path_archive) {
    	
    	System.out.println(path_archive);
    	var f = new File(path_archive);
    	f.mkdirs(); f.delete();
    	
        try (FileOutputStream fos = new FileOutputStream(path_archive);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipDirectory(baseDir, "", zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // Частично сгенерирован ИИ, я бы не трогал лишний раз, но оно работает
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

    /**
     * Destroy everything in this path directory and replace by contents of archive
     * */
    public static void DeArchivate(File baseDir, String path_archive) {

        File archiveFile = new File(path_archive);
        if (!archiveFile.exists()) {
            System.err.println("Архив не найден: " + path_archive);
            return;
        }

        // Clear this dir
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

        // Open archive and copy files from it
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
