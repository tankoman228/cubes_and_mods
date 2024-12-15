package com.cubes_and_mods.res.service_repos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.res.db.Machine;
import com.cubes_and_mods.res.db.Mineserver;
import com.cubes_and_mods.res.db.Tariff;
import com.cubes_and_mods.res.service_repos.repos.ReposMineservers;

@Service
public class ServiceMineservers {
    @Autowired
    private ReposMineservers repository;

    public Mineserver save(Mineserver i) {
        return repository.save(i);
    }

    public List<Mineserver> findAll() {
        return repository.findAll();
    }

    public Mineserver findById(Integer id) {
        return repository.findById(id).orElse(null); 
    }
    
    public List<Mineserver> findByIdOfUser(Integer id) {
        
    	if (id == null) {
    		return repository.findAll();
    	}
    	else {
    		return repository.findByIdOfUser(id);
    	}
    	
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
