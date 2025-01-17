package com.cubes_and_mods.usr;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.usr.db.User;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
    private UserService db;

	@GetMapping({"/all/", "/all"})
    public ResponseEntity<List<User>> all() {
		
		try {  					
			return ResponseEntity.ok(db.findAll());
	    }
    	catch (Exception ex) {
    		return ResponseEntity.status(500).body(null);
    	}
    }
	
	@PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody User usr) {
		
		try {  		
			if (!db.findByEmail(usr.getEmail()).isEmpty()) {
				return ResponseEntity.status(409).body(false);
			}
			usr.setPassword(PasswordHash.hash(usr.getPassword()));
			usr.setBanned(false);
			db.save(usr);
			return ResponseEntity.ok(true);
	    }
    	catch (Exception ex) {
    		return ResponseEntity.status(400).body(false);
    	}
    }
    
    @PostMapping("/auth")
    public ResponseEntity<User> auth(@RequestBody User user) {
    	System.err.println(user.getEmail()+" "+user.getPassword()+" "+user.getBanned());
    	
    	try {  		
	    	var usr = db.findByEmail(user.getEmail());
	    	
	    	if (usr.isEmpty())
	    		return ResponseEntity.status(404).body(null);
	    	
	    	if (usr.get().getBanned())
	    		return ResponseEntity.status(400).body(null);
	    	
	    	if (!PasswordHash.checkhash(usr.get().getPassword(), user.getPassword()))
	    		return ResponseEntity.status(403).body(null);
	    	System.err.println("Все хорошо");
	    	return ResponseEntity.ok(usr.get());
	    }
    	catch (NullPointerException ex) {
	    	System.err.println("Все плохо");
    		return ResponseEntity.status(400).body(null);
    	}
    }
    
    @PostMapping("/ban")
    public ResponseEntity<Boolean> ban(@RequestBody String email) {
    	
    	try {  		
	    	var usr = db.findByEmail(email);
	    	if (usr.isEmpty())
	    		return ResponseEntity.status(404).body(false);
	    	
	    	usr.get().setBanned(true);
	    	db.save(usr.get());

	    	return ResponseEntity.ok(true);
	    }
    	catch (NullPointerException ex) {
    		return ResponseEntity.status(400).body(false);
    	}
    }
    
    @PostMapping("/forgive/")
    public ResponseEntity<Boolean> forgive(@RequestBody String email) {
    	
    	try {  		
	    	var usr = db.findByEmail(email);
	    	if (usr.isEmpty())
	    		return ResponseEntity.status(404).body(false);
	    	
	    	usr.get().setBanned(false);
	    	db.save(usr.get());

	    	return ResponseEntity.ok(true);
	    }
    	catch (NullPointerException ex) {
    		return ResponseEntity.status(400).body(false);
    	}
    }
    
	@GetMapping("/generate_code")
    public ResponseEntity<String> generateCode(@RequestParam String email) {
    		/*
    	if (Calendar.getInstance().get(Calendar.SECOND) % 2 == 0) {
    		return ResponseEntity.status(666).body("Infernal Server Error");
    	}*/
		
		String newCode = PasswordHash.hash(email + Calendar.getInstance().toString() + "pplgond");		
    	codes.put(newCode, new Code(email));		
    	
    	return ResponseEntity.ok(newCode);  	
    }
	@PostMapping("/generate_code/")
    public ResponseEntity<String> generateCode_(@RequestBody String email) {
    	return generateCode(email);	
    }
    

	@GetMapping("/check_code")
    public ResponseEntity<String> checkCode(@RequestParam String code) {
    	
		if (!codes.containsKey(code))
			return ResponseEntity.status(666).body("Infernal Server Error");
		
		var code_ = codes.get(code);
		if (code_.Expired()) {
			codes.remove(code);
			return ResponseEntity.status(616).body("Infernal Server Error");
		}
			
		codes.remove(code);
		return ResponseEntity.ok(code_.email);
    }
	@PostMapping("/check_code/")
    public ResponseEntity<String> checkCode_(@RequestBody String code) {
		return checkCode(code);
    }
	
	private static Map<String, Code> codes = new HashMap<String, Code>();
	private class Code {
		
		private final int secondsBeforeExpired = 600;
			
		private Calendar expiredAt;	
		
		public String email;
		
		public Code(String email_) {		
			email = email_;
			expiredAt = Calendar.getInstance();
			expiredAt.add(Calendar.SECOND, secondsBeforeExpired);
		}
		
		public boolean Expired() {
			return expiredAt.before(Calendar.getInstance());
		}
	}
}
