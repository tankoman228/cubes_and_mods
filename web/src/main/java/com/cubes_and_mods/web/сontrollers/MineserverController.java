package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.web.jpa.Host;
import com.cubes_and_mods.web.web_clients.res.MineserverClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mcserver")
public class MineserverController {

	@Autowired
	MineserverClient mineserverClient;
	
	@GetMapping("/my")
	public Flux<Host> getMyMCServers(@RequestParam int id, HttpSession session)
	{
		String token = (String) session.getAttribute("email");
		return mineserverClient.getAllMineServers(id, token);
	}

	@GetMapping("/my/")
	public Mono<ResponseEntity<Host>> getByIdMineserver(@RequestParam int id, HttpSession session){
		String token = (String) session.getAttribute("email");
		return mineserverClient.getByIdMineserver(id, token);
	}

	@PutMapping("/my/edit")
	public Mono<ResponseEntity<Void>> edit(@RequestParam int id, @RequestBody Host host, HttpSession session){
		String token = (String) session.getAttribute("email");
		return mineserverClient.edit(id, host, token);
	}

	@PostMapping("/my/share")
	public Mono<ResponseEntity<Void>> share(@RequestParam int id, @RequestParam String email, HttpSession session){
		String token = (String) session.getAttribute("email");
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        if (email == null || !email.matches(emailRegex)) {
        	System.err.println("Вы ввели невалидную почту");
            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null));
        }
		System.out.println("Получен запрос на раздел сервера");
		return mineserverClient.share(id, email, token);
	}
}
