package com.cubes_and_mods.web.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cubes_and_mods.Clients.MailClient;

@Controller
@RequestMapping("/mail")
public class MailController {
	
    private final MailClient mailClient;

    @Autowired
    public MailController(MailClient mailClient) {
        this.mailClient = mailClient;
    }
	
	@GetMapping("/sendCode")
	public boolean sendCode(@RequestParam String email) {
		try {
			String code = mailClient.generateCode(email).toString();
			
			return true;
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
	}
}
