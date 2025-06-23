package com.cubes_and_mods.web.сontrollers.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/files")
public class FilesController {

	@Autowired
	FilesClient filesClient;
	
	@PostMapping("/all")
	public Mono<ResponseEntity<FileInfo>> getAllFiles(@RequestParam int id_server, HttpSession session){
		String token = (String) session.getAttribute("email");
		return filesClient.files(id_server, token);
	}
	
	//Скачивание!!!
	@PostMapping("/byPath")
	public Mono<ResponseEntity<FileInfo>> getFile(@RequestParam int id_server, @RequestParam String path, HttpSession session){
		if(path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")){
			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		String token = (String) session.getAttribute("email");
		return filesClient.file(id_server, path, token);
	}
	
	@PostMapping("/upload")
	public Mono<ResponseEntity<Void>> upload(@RequestParam int id_server, @RequestBody FileInfo file, HttpSession session){
		if(file.path.contains("user_jvm_args.txt") || file.path.contains("eula.txt") || file.path.contains("run.sh")){
			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		System.out.println("Это папка? " + file.isDirectory);
		String token = (String) session.getAttribute("email");
		System.out.println("Попытка загрузки файла");
		return filesClient.upload(id_server, file, token);
	}
	
	@PostMapping("/delete")
	public Mono<ResponseEntity<Void>> delete(@RequestParam int id_server, @RequestParam String path, HttpSession session){
		if(path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")){

			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		String token = (String) session.getAttribute("email");
		System.out.println("Выполнение delete запроса");
		System.out.println(id_server);
		System.out.println(path);
		return filesClient.delete(id_server, path, token);
	}

	@PutMapping("/move")
	public Mono<ResponseEntity<Void>> move(@RequestParam int id_server, @RequestBody String[] paths, HttpSession session){
		if(paths == null || paths.length != 2){
			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		for(var path : paths){
			if(path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")){

				return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
			}
		}
		String token = (String) session.getAttribute("email");
		return filesClient.move(id_server, paths, token);
	}

	@PutMapping("/copy")
	public Mono<ResponseEntity<Void>> copy(@RequestParam int id_server, @RequestBody String[] paths, HttpSession session){
		if(paths == null || paths.length != 2){
			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		for(var path : paths){
			if(path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")){

				return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
			}
		}
		String token = (String) session.getAttribute("email");
		return filesClient.copy(id_server, paths, token);
	}

	@PostMapping("/deleteRange")
	public Mono<ResponseEntity<Void>> deleteRange(@RequestParam int id_server, @RequestBody String[] paths, HttpSession session){
		String token = (String) session.getAttribute("email");
		
		for (String path : paths) {
			if (path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")) {
				return Mono.just(ResponseEntity.badRequest().build());
			}
		}

		return Flux.fromArray(paths)
			.flatMap(path -> {
				System.out.println("Выполнение delete запроса");
				System.out.println(id_server);
				System.out.println(path);
				return filesClient.delete(id_server, path, token)
					.onErrorResume(Throwable.class, error -> {
						System.out.println("Ошибка при удалении файла: " + error.getMessage());
						return Mono.error(new RuntimeException("Ошибка при удалении файла", error));
					});
			})
			.then(Mono.just(ResponseEntity.ok().<Void>build()))
			.onErrorResume(Throwable.class, error -> {
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
			});
	}
	
	//TODO: на удаление
	@GetMapping("/download")
	public Mono<ResponseEntity<ByteArrayResource>> download(@RequestParam int id_server, @RequestParam String path, HttpSession session){
		String token = (String) session.getAttribute("email");
		return filesClient.file(id_server, path, token)
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
	
	@PostMapping("/downloadZip")
	public Mono<ResponseEntity<ByteArrayResource>> downloadZip(@RequestParam int id_server, @RequestBody List<String> paths, HttpSession session) {
		String token = (String) session.getAttribute("email");

		return Flux.fromIterable(paths)
			.flatMap(path -> filesClient.file(id_server, path, token)
				.map(response -> response.getBody())
				.filter(fileInfo -> fileInfo != null && !fileInfo.isDirectory)
			)
			.collectList()
			.map(fileInfos -> {
				try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					ZipOutputStream zipStream = new ZipOutputStream(byteStream)) {

					for (FileInfo file : fileInfos) {
						ZipEntry entry = new ZipEntry(file.path.substring(file.path.lastIndexOf("/") + 1));
						zipStream.putNextEntry(entry);
						zipStream.write(file.contents);
						zipStream.closeEntry();
					}
					zipStream.finish();
					ByteArrayResource resource = new ByteArrayResource(byteStream.toByteArray());

					return ResponseEntity.ok()
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"archive.zip\"")
							.contentType(MediaType.APPLICATION_OCTET_STREAM)
							.contentLength(resource.contentLength())
							.body(resource);

				} catch (IOException e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(new ByteArrayResource(new byte[0]));
				}
			})
			.onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ByteArrayResource(new byte[0]))));
	}

	//Вовзращает текстовый контент
	@PostMapping("/getText")
	public Mono<ResponseEntity<FileInfoString>> getSettings(@RequestParam int id_server, @RequestParam String path, HttpSession session){
		if(path.contains("user_jvm_args.txt") || path.contains("eula.txt") || path.contains("run.sh")){
			return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		}
		
		String token = (String) session.getAttribute("email");
		return filesClient.file(id_server, path, token)
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
}
