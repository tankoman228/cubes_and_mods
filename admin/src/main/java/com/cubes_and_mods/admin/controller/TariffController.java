package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Tariff;
import com.cubes_and_mods.admin.jpa.repos.TariffRepos;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {
    
    @Autowired
    private TariffRepos tariffRepository;
    
    @GetMapping
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }
    
    @PostMapping
    public Tariff createTariff(@RequestBody Tariff tariff) {
        return tariffRepository.save(tariff);
    }
    
    @PutMapping("/{id}")
    public Tariff updateTariff(@PathVariable Integer id, @RequestBody Tariff tariffDetails) {
        Tariff tariff = tariffRepository.findById(id).get();
        
        if (!(tariff.getOrders().stream().anyMatch(x -> !x.getConfirmed()) || tariff.getHosts().size() > 0)) {  
            tariff.setRam(tariffDetails.getRam());
            tariff.setCpuThreads(tariffDetails.getCpuThreads());
            tariff.setMemoryLimit(tariffDetails.getMemoryLimit());
            tariff.setHoursWorkMax(tariffDetails.getHoursWorkMax());
        }

        tariff.setName(tariffDetails.getName());
        tariff.setCostRub(tariffDetails.getCostRub());
        tariff.setEnabled(tariffDetails.getEnabled());
        tariff.setMaxPlayers(tariffDetails.getMaxPlayers());
        
        return tariffRepository.save(tariff);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTariff(@PathVariable Integer id) {
        Tariff tariff = tariffRepository.findById(id).get();
        tariffRepository.delete(tariff);
        return ResponseEntity.ok().build();
    }
}