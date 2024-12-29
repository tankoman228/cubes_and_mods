package com.cubes_and_mods.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import com.cubes_and_mods.admin.db.Machine;

@RestController
public class RestApiController {
	
	private final static String FILE_PATH = "statistics"; // Путь к файлу
    private final ClientToOthers client = new ClientToOthers();

    @GetMapping("/api/machines")
    public List<Machine> getAllMachines() {
        return client.getAllMachines();
    }

    @GetMapping("/api/machines/{id}/stats")
    public List<Map<String, Object>> getMachineStats(@PathVariable Integer id) {
        List<Map<String, Object>> stats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH + id + ".csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Параметр id можно сопоставить с машиной, но для этого нужно, чтобы в строке был id
                // Добавим временной штамп, чтобы было поле для ID, например, в CSV первом столбце
                // Assume parts[0] содержит ID машины
                // if (machineId.equals(parts[0])) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("timestamp", parts[0]);
                    entry.put("ramFree", parts[1]);
                    entry.put("memoryFree", parts[2]);
                    entry.put("cpuThreadsFree", parts[3]);
                    stats.add(entry);
                // }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

        // Если нет статистики для заданного ID, возвращаем нули на текущую дату
        if (stats.isEmpty()) {
            return Collections.singletonList(createStatEntry(System.currentTimeMillis(), 0L, 0, 0));
        }
        
        return stats;
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
