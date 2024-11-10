package com.cubes_and_mods.res.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cubes_and_mods.res.service_repos.ServiceMachines;
import com.cubes_and_mods.res.db.*;

/**
 * COmment
 * */
@RestController
@RequestMapping("/machines")
public class ControllerMachines {

    @Autowired
    private ServiceMachines serviceMachines;

    
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
    	
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
