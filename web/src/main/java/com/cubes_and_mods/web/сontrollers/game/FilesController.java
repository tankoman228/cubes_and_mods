package com.cubes_and_mods.web.сontrollers.game;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.Clients.model.FileInfo;
import com.cubes_and_mods.web.Clients.model.FileInfoString;
import com.cubes_and_mods.web.web_clients.game.FilesClient;

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
		System.out.println("Попытка загрузки файла");
		return filesClient.upload(id_server, file);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Void>> delete(@RequestParam int id_server, @RequestParam String path){
		System.out.println("Выполнение delete запроса");
		System.out.println(id_server);
		System.out.println(path);
		return filesClient.delete(id_server, path);
	}
	
	@GetMapping("/download")
	public Mono<ResponseEntity<ByteArrayResource>> download(@RequestParam int id_server, @RequestParam String path){
		return filesClient.file(id_server, path)
				.flatMap(response -> {
					FileInfo fileInfo = response.getBody();
					if(fileInfo != null && fileInfo.isDirectory == false) {
						ByteArrayResource resource = new ByteArrayResource(fileInfo.contents);
						//System.out.println(fileInfo.text);
						return Mono.just(ResponseEntity.ok()
								.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileInfo.path + "\"")
				                .contentType(MediaType.APPLICATION_OCTET_STREAM)
				                .contentLength(fileInfo.contents.length)
								.body(resource));
					}
					return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ByteArrayResource(new byte[0])));
				})
				.onErrorResume(error -> {
					return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ByteArrayResource(new byte[0])));
				});
	}
	
	//Вовзращают текстовый контент
	@PostMapping("/getText")
	public Mono<ResponseEntity<FileInfoString>> getSettings(@RequestParam int id_server, @RequestParam String path){
		return filesClient.file(id_server, path)
				.flatMap(response -> {
					System.out.println("Начинаю получение файла");
					FileInfo fileInfo = response.getBody();
					if(fileInfo != null && fileInfo.isDirectory == false) {
						System.out.println("Файл получен");
						FileInfoString fileInfStr = new FileInfoString();
						fileInfStr.path = fileInfo.path;
						fileInfStr.isDirectory = fileInfo.isDirectory;
						fileInfStr.children = fileInfo.children;
						fileInfStr.size = fileInfo.size;
						fileInfStr.text = new String(fileInfo.contents, StandardCharsets.UTF_8);
						//System.out.println(fileInfStr.text);
						return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                .body(fileInfStr));
					}
					System.out.println("Файл равен нул");
					return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new FileInfoString()));
				})
				.onErrorResume(error -> {
					System.out.println("Произошла ошибка получения файла");
					System.err.println(error.getMessage());
					System.err.println(error.getCause());
					error.printStackTrace();
					return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new FileInfoString()));
				});
	}
	
	/*@PostMapping("/uploadText")
	public Mono<ResponseEntity<Object>> uploadSettings(@RequestParam int id_server, @RequestBody FileInfoString file){
		if(file == null || file.isDirectory == true) {
			return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null));
		}
		FileInfo fileInfo = new FileInfo();
		fileInfo.contents_bytes = file.text.getBytes();
		fileInfo.name = file.name;
		fileInfo.isDirectory = file.isDirectory;
		fileInfo.files = file.files;

		return filesClient.upload(id_server, file)
				.flatMap(response -> {
					return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                .body(null));
				})
				.onErrorResume(error -> {
					return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
				});
	}*/
	
}
