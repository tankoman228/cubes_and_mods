package com.cubes_and_mods.web.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubes_and_mods.web.Clients.MineserverClient;
import com.cubes_and_mods.web.Clients.RootClient;
import com.cubes_and_mods.web.DB.Mineserver;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;


@Controller
public class WebController {
	
	@Autowired
	MineserverClient mineserverClient;
	
	@Autowired
	RootClient rootClient;
	
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
    	try {
            String email = (String) session.getAttribute("email");
            if (email != null) {
                model.addAttribute("email", email);
                System.out.println(email);
            }

            return "index";
    	}
    	catch (Exception ex) {
    		System.out.println(ex.getMessage());
    		return(ex.getMessage());
    	}
    }
	
    @GetMapping("/signIn")
    public String signIn(Model model) {
    	try {
        	model.addAttribute("action", "signIn");
        	model.addAttribute("jsFile", "/js/signIn.js");
            return "sign";
    	}
    	catch (Exception ex) {
    		System.out.println(ex.getMessage());
    		return(ex.getMessage());
    	}
    }

    @GetMapping("/signUp")
    public String signUp(Model model) {
    	try {
            model.addAttribute("action", "signUp");
        	model.addAttribute("jsFile", "/js/signUp.js");
            return "sign";
    	}
    	catch (Exception ex) {
    		System.out.println(ex.getMessage());
    		return(ex.getMessage());
    	}
    }
    
    @GetMapping("/checkMail")
    public String checkMail() {
        return "checkMail";
    }
    
    @GetMapping("/console")
    public Mono<String> console(Model model, HttpSession session, @RequestParam int ServerId) {
    	/*if(session.getAttribute("email").toString().equals(null)) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
    		return "error";
    	}
    	
    	int UserId = (int)session.getAttribute("id");
    	
    	var Servers = mineserverClient.getAllMineServers(UserId);
    	if(Servers == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "У пользователя нет серверов");
    		return "error";
    	}
    	Mono<Mineserver> Server = Servers.filter(x -> x.getId().equals(ServerId)).singleOrEmpty();
    	
        final String[] serverNameHolder = new String[1];

        Server.subscribe(
                result -> {
                    serverNameHolder[0] = result.getName();
                    System.err.println(result.getName());
                    },
                error -> {
                    model.addAttribute("errorCode", 400);
                    model.addAttribute("errorMessage", error.getMessage());
                },
                () -> {
                    model.addAttribute("errorCode", 500);
                    model.addAttribute("errorMessage", "Произошла непредвиденная ошибка");
                }
        );
        
        if(model.getAttribute("errorMessage") != null) {
        	return "error";
        }
    	
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("srvID", ServerId);
        model.addAttribute("srvName", serverNameHolder[0]);
        return "console";*/
    	
    	String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
            return Mono.just("error");
        }

        int UserId = (int) session.getAttribute("id");
        
        return mineserverClient.getAllMineServers(UserId)
            .filter(x -> x.getId().equals(ServerId))
            .singleOrEmpty()
            .flatMap(server -> {
                var path = rootClient.mineserverInstalled(ServerId)
	                .map(responseEntity -> {
                    	model.addAttribute("id", session.getAttribute("id"));
                        model.addAttribute("email", session.getAttribute("email"));
                        model.addAttribute("srvID", ServerId);
                        model.addAttribute("srvName", server.getName());
                        
	                    if (responseEntity.getBody() != null && responseEntity.getBody()) {
	                    	return "console";
	                    } else {
	                        return "MCVersions";
	                    }
	                })
	                .doOnError(err -> {
	                    model.addAttribute("errorCode", 404);
	                    model.addAttribute("errorMessage", err.getMessage());
	                })
	                .onErrorReturn("error");
                
                return path;
            })
            .switchIfEmpty(Mono.defer(() -> {
                model.addAttribute("errorCode", 404);
                model.addAttribute("errorMessage", "У пользователя нет серверов или сервер не найден");
                return Mono.just("error");
            }))
            .onErrorResume(error -> {
                model.addAttribute("errorCode", 400);
                model.addAttribute("errorMessage", error.getMessage());
                return Mono.just("error");
            });
    }
    
    @GetMapping("/myServers")
    public String mcServers(Model model, HttpSession session) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
    		return "error";
        }
    	
    	model.addAttribute("id", session.getAttribute("id"));
    	model.addAttribute("email", session.getAttribute("email"));

    	return "MyMCServers";
    }
    
    @GetMapping("/buyTariff")
    public String buyTariff(Model model, HttpSession session, @RequestParam int tariffId) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
    		return "error";
        }
    	
        model.addAttribute("id", session.getAttribute("id"));
    	model.addAttribute("email", session.getAttribute("email"));
    	model.addAttribute("tariffId", tariffId);
    	return "buyTariff";
    }
    
    @GetMapping("/payOrder")
    public String payOrder(Model model, HttpSession session, @RequestParam int tariffId, @RequestParam int machineId, @RequestParam String key) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
    		return "error";
        }
        
        System.err.println("На веб контроллере: " + key);
    	
        model.addAttribute("id", session.getAttribute("id"));
    	model.addAttribute("email", session.getAttribute("email"));
    	model.addAttribute("tariffId", tariffId);
    	model.addAttribute("key", key);
    	model.addAttribute("machineId", machineId);
    	return "payOrder";
    }
}
