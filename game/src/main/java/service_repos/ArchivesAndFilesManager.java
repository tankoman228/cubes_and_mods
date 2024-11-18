package service_repos;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

public class ArchivesAndFilesManager {

    public static final int FILE_CHUNK_SIZE = 8192;

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
                        byte[] buffer = new byte[FILE_CHUNK_SIZE];
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
     * Sends file by parts
     * */
    public static void SendFileByParts(File file, String http_address) {
    	
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[FILE_CHUNK_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                sendChunk(http_address, buffer, bytesRead);
            }
            // Send "null" to indicate the end of the file transfer
            sendChunk(http_address, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendChunk(String http_address, byte[] chunk, int length) throws IOException {
        
    	HttpURLConnection connection = (HttpURLConnection) new URL(http_address).openConnection();
    	
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setRequestProperty("Content-Length", String.valueOf(length));

        try (OutputStream os = connection.getOutputStream()) {
            if (chunk != null) {
                os.write(chunk, 0, length);
            }
        }

        // Optionally handle the response
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.err.println("Failed to send chunk: " + responseCode);
        }
    }

    public static void GetFileByParts(String path, byte[] writeBytes) {
    	
        try (FileOutputStream fos = new FileOutputStream(path, true)) { 
        	
            //if (writeBytes != null) {
                fos.write(writeBytes);
            //} else {
                // Handle null case if necessary
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DeArchivate(File baseDir, String path_archive) {        
    	
    	try {
    		
    		ZipInputStream zis = new ZipInputStream(new FileInputStream(path_archive));
	        ZipEntry zipEntry;
	        while ((zipEntry = zis.getNextEntry()) != null) {
	            File newFile = new File(baseDir, zipEntry.getName());
	            if (zipEntry.isDirectory()) {
	                newFile.mkdirs();
	            } else {
	                // Create parent directories if needed
	                new File(newFile.getParent()).mkdirs();
	                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
	                    byte[] buffer = new byte[FILE_CHUNK_SIZE];
	                    int len;
	                    while ((len = zis.read(buffer)) > 0) {
	                        bos.write(buffer, 0, len);
	                    }
	                }
	            }
	            zis.closeEntry();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
