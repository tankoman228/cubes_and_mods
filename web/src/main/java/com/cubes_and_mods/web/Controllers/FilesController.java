package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.FilesClient;
import com.cubes_and_mods.web.Clients.model.FileInfo;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/files")
public class FilesController {

	@Autowired
	FilesClient filesClient;
	
	@PostMapping("/all")
	public Mono<ResponseEntity<FileInfo>> getAllFiles(@RequestParam int id_server){
		return filesClient.files(id_server);
	}
	
	//Скачивание!!!
	@PostMapping("/byPath")
	public Mono<ResponseEntity<FileInfo>> getFile(@RequestParam int id_server, @RequestParam String path){
		return filesClient.file(id_server, path);
	}
	
	@PostMapping("/upload")
	public Mono<ResponseEntity<Void>> upload(@RequestParam int id_server, @RequestBody FileInfo file){
		return filesClient.upload(id_server, file);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Void>> delete(@RequestParam int id_server, @RequestParam String path){
		return filesClient.delete(id_server, path);
	}
	
	//Вовзращают текстовый контент
	@PostMapping("/getSetings")
	public Mono<ResponseEntity<FileInfo>> getSettings(@RequestParam int id_server, @RequestParam String path){
		return filesClient.file(id_server, path);
	}
	
	@PostMapping("/uploadSetings")
	public Mono<ResponseEntity<FileInfo>> uploadSettings(@RequestParam int id_server, @RequestParam String path){
		return filesClient.file(id_server, path);
	}
}
