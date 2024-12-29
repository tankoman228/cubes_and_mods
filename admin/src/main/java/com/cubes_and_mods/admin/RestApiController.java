package com.cubes_and_mods.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.db.Machine;

@RestController
public class RestApiController {
	
	
	   private List<Machine> TestMachines() {
		   
		   Machine m = new Machine();
		   Machine m2 = new Machine();
		   
		   m.setId(1);
		   m2.setId(3);
		   
		   m.setName("name1");
		   m2.setName("name2");
		   
		   return Arrays.asList(m, m2);
	   }

	   @GetMapping("/api/machines")
	   public List<Machine> getAllMachines() {
	       return TestMachines();
	   }

	   @GetMapping("/api/machines/{id}/stats")
	   public List<Map<String, Object>> getMachineStats(@PathVariable Integer id) {
	       // Здесь возвращаем тестовые данные по статистике
		   
		   Random rand = new Random();
		   
	       return Arrays.asList(
	               createStatEntry(System.currentTimeMillis(), 51L, rand.nextInt(), 4),
	               createStatEntry(System.currentTimeMillis() + 1000, rand.nextInt(), 3, 7),
	               createStatEntry(System.currentTimeMillis() + 9000, rand.nextInt(), 5, id * 8 + 3)
	       );
	   }

	   private Map<String, Object> createStatEntry(long timestamp, long memoryUsed, int freeCpuThreads, int ramFree) {
	       Map<String, Object> entry = new HashMap<>();
	       entry.put("timestamp", timestamp);
	       entry.put("memoryFree", memoryUsed);
	       entry.put("cpuThreadsFree", freeCpuThreads);
	       entry.put("ramFree", ramFree);
	       return entry;
	   }
}
