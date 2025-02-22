package com.cubes_and_mods.host.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.service.ServiceHandlers;
import com.cubes_and_mods.host.service.mineserver_process.IMinecraftHandler;

/**
 * Files of minecraft servers
 * */
@RestController
@RequestMapping("/files")
public class FilesController {

	@Autowired
	ServiceHandlers ServiceHandlers;
	
	@PostMapping("/{id_server}")
	public ResponseEntity<FileInfo> files(@PathVariable int id_server) {
		
		var handler = getHandler (id_server);
		return new ResponseEntity<>(new FileInfo(handler.GetFilesTree(), false), HttpStatus.OK);
	}
	
	@PostMapping("/read/{id_server}")
	public ResponseEntity<FileInfo> file(@RequestBody String path, @PathVariable int id_server) {
		
		var handler = getHandler (id_server);
		return new ResponseEntity<>(new FileInfo(handler.GetFile(path), true), HttpStatus.OK);
	}
	
	@PostMapping("/upload/{id_server}")
	public ResponseEntity<Void> upload(@RequestBody FileInfo file, @PathVariable int id_server) {
		var handler = getHandler (id_server);
		WriteToHandler(handler, file, "");
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/delete/{id_server}")
	public ResponseEntity<Void> delete(@RequestBody String path, @PathVariable int id_server) {
		
		var handler = getHandler (id_server);
		handler.DeleteFile(path);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private IMinecraftHandler getHandler(int id_server) {
		return null;
	}
	
	/**
	 * Recursive adding files to IMinecraftHandler (Mine server directory)
	 * Creates directories and files
	 * */
	private void WriteToHandler(IMinecraftHandler handler, FileInfo file, String curPath) {
		
	    if (file.isDirectory) {
	    	
	        String dirPath = curPath + "/" + file.name; 
	        handler.CreateDirIfNotExists(dirPath); 
	        
	        for (var f : file.files) {
	            WriteToHandler(handler, f, dirPath); 
	        }
	    } 
	    else {
	    	
	        String filePath = curPath + "/" + file.name; 
	        handler.CreateFile(filePath, file.contents_bytes); 
	    }
	}
	
	
	private static class FileInfo {
		
		public boolean isDirectory;
		public List<FileInfo> files;
		//public String contents;
		public byte[] contents_bytes;
		public String name;
		
		public FileInfo() {
			
		}
		
		public FileInfo(File file, boolean readFileContents) {
			
			isDirectory = file.isDirectory();
			name = file.getName();
			
			if (isDirectory) {
							
				files = new ArrayList<>();
				
				for (var f: file.listFiles()) {
					files.add(new FileInfo(f, readFileContents));
				}
			}
			else if (readFileContents) {
				
				try {
					contents_bytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
					// Files.write(Path.of("go/truck/yourself.exe"), contents_bytes);
					//contents = Files.readString(Path.of(file.getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
