package com.cubes_and_mods.buy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.buy.db.Tariff;
import com.cubes_and_mods.buy.service_repos.ServiceTariff;

@RestController
@RequestMapping("/tariffs")
public class TariffController {

    @Autowired
    private ServiceTariff tariffService;

    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        List<Tariff> tariffs = tariffService.findAll();
        return new ResponseEntity<>(tariffs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tariff> getTariffById(@PathVariable Integer id) {
        Tariff tariff = tariffService.findById(id);
        return tariff != null ? new ResponseEntity<>(tariff, HttpStatus.OK) 
                              : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        Tariff savedTariff = tariffService.save(tariff);
        return new ResponseEntity<>(savedTariff, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tariff> updateTariff(@PathVariable Integer id, @RequestBody Tariff tariff) {
    	
    	System.out.println(tariff.getEnabled());
    	
        Tariff existingTariff = tariffService.findById(id);
        if (existingTariff == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        tariff.setId(id); 
        Tariff updatedTariff = tariffService.save(tariff);
        return new ResponseEntity<>(updatedTariff, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Integer id) {
        tariffService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
