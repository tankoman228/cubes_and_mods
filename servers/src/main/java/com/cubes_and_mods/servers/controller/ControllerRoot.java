package com.cubes_and_mods.servers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.servers.service_repos.ServiceMachines;
import com.cubes_and_mods.servers.service_repos.ServiceMineservers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CRUD + resource reserving
 * + conditions checkers "if this server has enough resources for a new server with tariff X"
 * */
@RestController
@RequestMapping
public class ControllerRoot {

	@PostMapping("verify_ssl")
	public ResponseEntity<VerifyWebResponce> verif(@RequestBody VerifyWebRequest r) { 	
		return new ResponseEntity<>(new VerifyWebResponce(r.a + r.b), HttpStatus.OK); 
	}
    public static class VerifyWebRequest { 
        public int a, b;
    }
    public static class VerifyWebResponce {
        public int c;
        @JsonCreator
        public VerifyWebResponce (@JsonProperty("c") int c) {
            this.c = c;
        }
        public VerifyWebResponce () {}
    }
	
	@GetMapping()
	public ResponseEntity<Void> all(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("logs")
	public ResponseEntity<Void> logs(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
	
	@GetMapping("{id}")
	public ResponseEntity<Void> id(){ return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

	/*
	
    @Autowired
    private ServiceMachines serviceMachines;

    @Autowired
    private ServiceMineservers serviceMineserver;
    
    
    @GetMapping
    public ResponseEntity<List<Machine>> getAllMachines() {
    	
        List<Machine> machines = serviceMachines.findAll();
        return new ResponseEntity<>(machines, HttpStatus.OK);
    }

    @PostMapping("/which_can")
    public ResponseEntity<List<Machine>> whichCan(@RequestBody Tariff tariff) {
    	
    	return new ResponseEntity<>(serviceMachines.whichCan(tariff), HttpStatus.OK);
    }

    @PostMapping("/can_handle/{id_machine}/{id_tariff}")
    public ResponseEntity<Boolean> canHandle(@PathVariable int id_machine, @PathVariable int id_tariff) {
    	
    	return new ResponseEntity<>(serviceMachines.canHandle(id_machine, id_tariff), HttpStatus.OK);
    }   
    
    @PostMapping("/can_update_tariff/{id_mineserver}/{id_tariff}")
    public ResponseEntity<Boolean> canHandleWithNewTariff(@PathVariable int id_mineserver, @PathVariable int id_tariff) {
    	
    	var mineserver = serviceMineserver.findById(id_mineserver);
    	
    	return new ResponseEntity<>(
    			serviceMachines.canReplaceTariff(
    					mineserver.getIdMachine(), 
    					mineserver.getIdTariff(), 
    					id_tariff), 
    			HttpStatus.OK);
    }
    
    // Reserve resources for a new server. Called by "buy" service 
    @PostMapping("/reserve/{id}")
    public ResponseEntity<Void> reserve(@PathVariable Integer id, @RequestBody Tariff tariff) {
        
    	boolean r = serviceMachines.tryReserve(tariff, serviceMachines.findById(id));
    	 	
    	if (r) {
    		return new ResponseEntity<>(HttpStatus.OK);
    	}
    	else {
    		return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    	}
    }
    
    // Caution: no data check! Called by "buy" service if after "reserve" the order was cancelled
    @PostMapping("/free/{id}")
    public ResponseEntity<Void> free(@PathVariable Integer id, @RequestBody Tariff tariff) {
        
    	serviceMachines.free(tariff, serviceMachines.findById(id));

    	return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/recount")
    public ResponseEntity<Void> recount(@RequestBody Integer id) {
        // recalculates data about resources
		Machine existingMachine = serviceMachines.findById(id);
		serviceMachines.recount(existingMachine);
    	
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    
    // Standart generated CRUD
    
    @GetMapping("/{id}")
    public ResponseEntity<Machine> getMachineById(@PathVariable Integer id) {
    	
        Machine machine = serviceMachines.findById(id);
        return machine != null ? new ResponseEntity<>(machine, HttpStatus.OK) 
                               : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
    	
        Machine savedMachine = serviceMachines.save(machine);
        return new ResponseEntity<>(savedMachine, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Machine> updateMachine(@PathVariable Integer id, @RequestBody Machine machine) {
    	
        Machine existingMachine = serviceMachines.findById(id);
        if (existingMachine == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        machine.setId(id); 
        Machine updatedMachine = serviceMachines.save(machine);
        return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Integer id) {
    	
        serviceMachines.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
}
