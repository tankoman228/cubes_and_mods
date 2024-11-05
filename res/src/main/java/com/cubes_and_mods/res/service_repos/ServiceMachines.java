package com.cubes_and_mods.res.service_repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.res.db.Machine;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceMachines {
	
    @Autowired
    private ReposMachines machinesRepository;

    public Machine save(Machine i) {
        return machinesRepository.save(i);
    }

    public List<Machine> findAll() {
        return machinesRepository.findAll();
    }

    public Machine findById(Integer id) {
        return machinesRepository.findById(id).orElse(null); 
    }

    public void delete(Integer id) {
        machinesRepository.deleteById(id);
    }
}
