package com.cubes_and_mods.buy.service_repos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.buy.db.Tariff;

@Service
public class ServiceTariff {
	
	@Autowired
    private ReposTariff tariffRepository;

    public Tariff save(Tariff i) {
        return tariffRepository.save(i);
    }

    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }

    public Tariff findById(Integer id) {
        return tariffRepository.findById(id).orElse(null); 
    }

    public void delete(Integer id) {
        tariffRepository.deleteById(id);
    }
}
