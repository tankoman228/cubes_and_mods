package com.cubes_and_mods.servers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.cubes_and_mods.servers.jpa.Version;

import jakarta.transaction.Transactional;


/**
 * Archivates into a huge byte array of Version object data from minecraft server template directory 
 * */
public class VersionZipper {

    /**
     * Get archive a new Version object of model with binary data of archive
     * CAUTION: LARGE BLOBS
     * */
    @Transactional
    public static Version uploadVersion(String name, String description, String directoryPath) throws IOException {
        
    	File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Provided path is not a valid directory.");
        }

        Version version = new Version();
        version.setName(name);
        version.setDescription(description);
        version.setArchive(zipDirectory(directory));

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
}