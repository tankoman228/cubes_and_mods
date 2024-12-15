package com.cubes_and_mods.res.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.res.service_repos.ServiceMachines;
import com.cubes_and_mods.res.service_repos.ServiceMineservers;
import com.cubes_and_mods.res.db.*;

/**
 * Comment (I am lazy rabbit)
 * */
@RestController
@RequestMapping("/machines")
public class ControllerMachines {

    @Autowired
    private ServiceMachines serviceMachines;

    @Autowired
    private ServiceMineservers serviceMineserver;
    
    
    @GetMapping
    public ResponseEntity<List<Machine>> getAllMachines() {
    	
        List<Machine> machines = serviceMachines.findAll();
        return new ResponseEntity<>(machines, HttpStatus.OK);
    }

    
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
    
    @PostMapping("/free/{id}")
    public ResponseEntity<Void> free(@PathVariable Integer id, @RequestBody Tariff tariff) {
        
    	serviceMachines.free(tariff, serviceMachines.findById(id));

    	return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/recount")
    public ResponseEntity<Void> recount(@RequestBody Integer id) {
        
    	if (id == null) {
    		
    		var machines = serviceMachines.findAll();
    		for (var  existingMachine: machines) {
    			serviceMachines.recount(existingMachine);
    		}
    	}
    	else {
    		Machine existingMachine = serviceMachines.findById(id);
    		serviceMachines.recount(existingMachine);
    	}
    	
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
