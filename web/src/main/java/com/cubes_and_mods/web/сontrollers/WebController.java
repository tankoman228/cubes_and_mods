package com.cubes_and_mods.web.сontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubes_and_mods.web.dto.Host;
import com.cubes_and_mods.web.security.ClientSession;
import com.cubes_and_mods.web.web_clients.MailClient;
import com.cubes_and_mods.web.web_clients.UserClient;
import com.cubes_and_mods.web.web_clients.game.RootClient;
import com.cubes_and_mods.web.web_clients.res.MineserverClient;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@Controller
public class WebController {

	@Autowired
	MineserverClient mineserverClient;
	
	@Autowired
	RootClient rootClient;
	
	@Autowired
    MailClient mailClient;
	
	@Autowired
    UserClient userClient;
	
	
	// TODO: пж, разнеси этот класс на несколько, а то читать и разбираться в нём - полный капец
	
	// =-=-=- REGION ENTER PAGE -=-=-=-=
	
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
    	try {
            String email = (String) session.getAttribute("email");
            System.out.println(session == null);
            if (email != null) {
                System.out.println("Пользователь вошел");
                model.addAttribute("email", email);
                System.out.println(email);
            }
            else{
                System.out.println("Пользователь не вошел");
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
    public Mono<String> checkMail() {
        return Mono.just("checkMail");
    }
    
    @GetMapping("/checkCode")
	public Mono<String> checkCode(Model model, HttpSession session, @RequestParam String code) {

		return userClient.checkCode(code).flatMap(response -> {
			
			System.out.println("Удачно: "+response.getStatusCode()+" "+response.getBody());
			
			return userClient.get(response.getBody()).flatMap(userB -> {
				
				ClientSession clientSession = userB.getBody();
				
				session.setAttribute("id", clientSession.client.getId());
                //session.setAttribute("email", clientSession.client.getEmail());

                model.addAttribute("email", session.getAttribute("email"));
				
				return Mono.just("checkingEmail");
				
			}).onErrorResume(userError -> {
	             return Mono.just("error");
	         });
		}).onErrorResume(error -> {
            if (error.getMessage().contains("666")) {
            	System.err.println("666");
                model.addAttribute("errorCode", 666);
                model.addAttribute("errorMessage", "Введен неверный код: " + code);
                return Mono.just("error");
            }
            if (error.getMessage().contains("616")) {
            	System.err.println("616");
                model.addAttribute("errorCode", 616);
                model.addAttribute("errorMessage", "Данный код просрочен: " + code);
                return Mono.just("error");
            }
        	System.err.println(error.getMessage());
            model.addAttribute("errorCode", 500);
            model.addAttribute("errorMessage", "Ошибка проверки кода: " + error.getMessage());
            return Mono.just("error");
		});
	}
    
    
    // =-=-=- REGION MINECRAFT SERVERS -=-=-=-=
    
    @GetMapping("/console")
    public Mono<String> console(Model model, HttpSession session, @RequestParam int ServerId) { 
    	return forServer(model, session, ServerId, "console");
    }
    
    @GetMapping("/myServers")
    public String mcServers(Model model, HttpSession session) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
        	return notAuthError(model);
        }
    	
    	model.addAttribute("id", session.getAttribute("id"));
    	model.addAttribute("email", session.getAttribute("email"));

    	return "MyMCServers";
    }
    
    
    // =-=-=- REGION ORDERS -=-=-=-=
    
    @GetMapping("/buyTariff")
    public String buyTariff(Model model, HttpSession session, @RequestParam int tariffId) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
        	return notAuthError(model);
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
        	return notAuthError(model);
        }
        
        System.err.println("На веб контроллере: " + key);
    	
        model.addAttribute("id", session.getAttribute("id"));
    	model.addAttribute("email", session.getAttribute("email"));
    	model.addAttribute("tariffId", tariffId);
    	model.addAttribute("key", key);
    	model.addAttribute("machineId", machineId);
    	return "payOrder";
    }
    
    
    // =-=-=- REGION OTHER FEATURES -=-=-=-=
    
    @GetMapping("/about")
    public Mono<String> about(Model model, HttpSession session) {
        model.addAttribute("email", session.getAttribute("email"));
        return Mono.just("about");
    }
    
    @GetMapping("/tariffs")
    public Mono<String> tariffs(Model model, HttpSession session) {
        model.addAttribute("email", session.getAttribute("email"));
        return Mono.just("tariffs");
    }
    
    @GetMapping("/serverSettings")
    public Mono<String> settings(Model model, HttpSession session, @RequestParam int ServerId) {
    	return forServer(model, session, ServerId, "settings");
    }
    
    @GetMapping("/serverBackup")
    public Mono<String> backups(Model model, HttpSession session, @RequestParam int ServerId) {
    	return forServer(model, session, ServerId, "backups");
    }
    
    @GetMapping("/serverFiles")
    public Mono<String> files(Model model, HttpSession session, @RequestParam int ServerId) {
    	return forServer(model, session, ServerId, "files");
    }
    
    @GetMapping("/changeTariff")
    public Mono<String> changeTarif(Model model, HttpSession session, @RequestParam int ServerId) {
    	return forServer(model, session, ServerId, "changeTariff");
    }
    
    @GetMapping("/textReader")
    public Mono<String> text(Model model, HttpSession session, @RequestParam int ServerId, @RequestParam String path) {
    	String email = (String) session.getAttribute("email");
        if (email == null) {
        	return Mono.just(notAuthError(model));
        }
        
    	model.addAttribute("id", session.getAttribute("id"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("srvID", ServerId);
        model.addAttribute("path", path);
    	return Mono.just("textReader");
    }
    
    private String notAuthError(Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", "Вы не вошли в систему");
		return "error";
    }
    
    private Mono<String> forServer(Model model, HttpSession session, @RequestParam int ServerId, String page) {    	
    	String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("errorCode", 404);
            model.addAttribute("errorMessage", "Вы не вошли в систему");
            return Mono.just("error");
        }

        int UserId = (int) session.getAttribute("id");
        
        return mineserverClient.getByIdMineserver(ServerId)
            .flatMap(responseHost -> {
                Host host = responseHost.getBody();
                var path = rootClient.mineserverInstalled(ServerId)
	                .map(responseIsInstaled -> {
                        if(host == null){
                            model.addAttribute("errorCode", 404);
	                        model.addAttribute("errorMessage", "Ошибка при получении сервера");
                            return "error";
                        }
                        System.out.println(host.getTariffHost().getName());
                    	model.addAttribute("id", session.getAttribute("id"));
                    	//System.err.println("Передача ID пользователя " + session.getAttribute("id"));
                        model.addAttribute("email", session.getAttribute("email"));
                        model.addAttribute("srvID", ServerId);
                        model.addAttribute("srvName", host.getName());
                        model.addAttribute("srvTariff", host.getTariffHost().getId());
                        model.addAttribute("srvSeconds", host.getSecondsWorking());
                        model.addAttribute("srvMem", host.getMemoryUsed());

                        Boolean isInstalled = responseIsInstaled.getBody();

                        if(isInstalled == null){
                            model.addAttribute("errorCode", 404);
	                        model.addAttribute("errorMessage", "Ошибка при получении статуса сервера");
                            return "error";
                        }

                        System.out.println("Сервер установлен " + ServerId + " : " + isInstalled);

	                    if (isInstalled == true) {
	                    	return page;
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
            .onErrorResume(error -> {
                model.addAttribute("errorCode", 400);
                model.addAttribute("errorMessage", error.getMessage());
                return Mono.just("error");
            });

        /*return mineserverClient.getAllMineServers(UserId)
            .filter(x -> x.getId().equals(ServerId))
            .singleOrEmpty()
            .flatMap(server -> {
                var path = rootClient.mineserverInstalled(ServerId)
	                .map(responseEntity -> {
                        System.out.println(server.getIdTariff());
                    	model.addAttribute("id", session.getAttribute("id"));
                    	//System.err.println("Передача ID пользователя " + session.getAttribute("id"));
                        model.addAttribute("email", session.getAttribute("email"));
                        model.addAttribute("srvID", ServerId);
                        model.addAttribute("srvName", server.getName());
                        model.addAttribute("srvTariff", server.getIdTariff());
                        model.addAttribute("srvSeconds", server.getSecondsWorking());
                        model.addAttribute("srvMem", server.getMemoryUsed());

                        Boolean isInstalled = responseEntity.getBody();

                        if(isInstalled == null){
                            model.addAttribute("errorCode", 404);
	                        model.addAttribute("errorMessage", "Ошибка при получении статуса сервера");
                            return "error";
                        }

                        System.out.println("Сервер установлен " + ServerId + " : " + isInstalled);

	                    if (isInstalled == true) {
	                    	return page;
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
            /*.switchIfEmpty(Mono.defer(() -> {
                model.addAttribute("errorCode", 404);
                model.addAttribute("errorMessage", "У пользователя нет серверов или сервер не найден");
                return Mono.just("error");
            }))
            .onErrorResume(error -> {
                model.addAttribute("errorCode", 400);
                model.addAttribute("errorMessage", error.getMessage());
                return Mono.just("error");
            });*/
    }
}
