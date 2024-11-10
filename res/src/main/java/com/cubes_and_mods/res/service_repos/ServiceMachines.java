package com.cubes_and_mods.res.service_repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.res.db.Machine;
import com.cubes_and_mods.res.db.Tariff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceMachines {
	
    @Autowired
    private ReposMachines machinesRepository;

    @Autowired
    private ReposMineservers mineserversRepository;
    
    @Autowired
    private ReposTariffs tariffsRepository;
    
    
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
    
    
    public List<Machine> whichCan(Tariff tariff) {
    	
    	List<Machine> machines = new ArrayList<>();
    	
    	for (var m: machinesRepository.findAll()) {
    		
    		if (canHandle(m, tariff)) {
    			machines.add(m);
    		}
    	}
    	
    	return machines;
    }
    
    public boolean tryReserve(Tariff tariff, Machine machine) {
    	
    	if (!canHandle(machine, tariff))
    		return false;
    	
    	machine.setCpuThreadsFree((short) (machine.getCpuThreadsFree() - tariff.getCpuThreads()));
    	machine.setRamFree((short)(machine.getRamFree() - tariff.getRam()));
    	machine.setMemoryFree(machine.getMemoryFree() - tariff.getMemoryLimit());
    	
    	machinesRepository.saveAndFlush(machine);
    	
    	return true;
    }
    
    public void recount(Machine machine) {
    	   	
    	short removeRam = 0;
    	short removeCpu = 0;
    	int removeMemory = 0;
    	
    	var mineservers = mineserversRepository.findByIdOfMachine(machine.getId());
    	for (var m: mineservers) {
    		
    		var t = tariffsRepository.findById(m.getIdTariff()).get();
    		
    		removeRam += t.getRam();
    		removeCpu += t.getCpuThreads();
    		removeMemory += t.getMemoryLimit();
    	}
    	
    	machine.setCpuThreadsFree((short) (machine.getCpuThreads() - removeCpu));
    	machine.setRamFree((short) (machine.getRam() - removeRam));
    	machine.setMemoryFree(machine.getMemory() - removeMemory);
    	
    	machinesRepository.saveAndFlush(machine);
    }
    
    private boolean canHandle(Machine machine, Tariff tariff) {
    	
    	if (machine.getRamFree() - tariff.getRam() < 0)
    		return false;
    	
    	if (machine.getCpuThreadsFree() - tariff.getCpuThreads() < 0)
    		return false;
    	
    	if (machine.getMemoryFree() - tariff.getMemoryLimit() < 0)
    		return false;
    	
    	return true;
    }
}
