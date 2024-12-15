package com.cubes_and_mods.res.service_repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.res.db.Machine;
import com.cubes_and_mods.res.db.Tariff;
import com.cubes_and_mods.res.service_repos.repos.ReposMachines;
import com.cubes_and_mods.res.service_repos.repos.ReposMineservers;
import com.cubes_and_mods.res.service_repos.repos.ReposTariffs;

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
    
	public void free(Tariff tariff, Machine machine) {

    	machine.setCpuThreadsFree((short) (machine.getCpuThreadsFree() + tariff.getCpuThreads()));
    	machine.setRamFree((short)(machine.getRamFree() + tariff.getRam()));
    	machine.setMemoryFree(machine.getMemoryFree() + tariff.getMemoryLimit());
    	
    	machinesRepository.saveAndFlush(machine);
		
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
    
    public boolean canHandle(Integer id_machine, Integer id_tariff) {
    	
    	var machine = machinesRepository.findById(id_machine).get();
    	var tariff = tariffsRepository.findById(id_tariff).get();
    	
    	return canHandle(machine, tariff);
    }
    
    public boolean canReplaceTariff(Integer id_machine, Integer id_tariff_now, Integer id_tariff_new) {
    	
    	var machine = machinesRepository.findById(id_machine).get();
    	var tariff_now = tariffsRepository.findById(id_tariff_now).get();
    	var tariff_new = tariffsRepository.findById(id_tariff_new).get();
    	
    	// If new tariff means 6 GB, but old is 4 GB, that equals handling additional 6 - 4 = 2 GB 
    	var tariff_virtual = new Tariff();
    	tariff_virtual.setCpuThreads((short) (tariff_new.getCpuThreads() - tariff_now.getCpuThreads()));
    	tariff_virtual.setMemoryLimit(tariff_new.getMemoryLimit() - tariff_now.getMemoryLimit());
    	tariff_virtual.setRam((short) (tariff_new.getRam() - tariff_now.getRam()));
    	
    	return canHandle(machine, tariff_virtual);
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
