package com.cubes_and_mods.host.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.host.jpa.Backup;
import com.cubes_and_mods.host.security.ProtectedRequest;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins;
import com.cubes_and_mods.host.security.annotations.AllowedOrigins.MService;
import com.cubes_and_mods.host.service.ServiceBackup;
/**
 * Work to minecraft server backups
 * */
@RestController
@RequestMapping("/backup")
public class BackupController {
	
    @Autowired
    private ServiceBackup serviceBackup;

    @PostMapping("/{id_host}/all")
    @AllowedOrigins(MService.WEB)
    public ResponseEntity<List<Backup>> getAllBackups(@RequestBody ProtectedRequest<Void> request, 
                                                     @PathVariable Integer id_host) {
        return ResponseEntity.ok(serviceBackup.getAllBackupsForHost(id_host));
    }

    @PostMapping("/{id_host}")
    @AllowedOrigins(MService.WEB)
    public ResponseEntity<Integer> createBackup(@RequestBody ProtectedRequest<String> request,
                                               @PathVariable Integer id_host) {
        try {
            int operationId = serviceBackup.createBackup(id_host, request.data, request);
            return ResponseEntity.ok(operationId);
        } catch (Exception e) {
			System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id_host}/rollback/{id_back}")
    @AllowedOrigins(MService.WEB)
    public ResponseEntity<Integer> rollback(@RequestBody ProtectedRequest<Void> request,
                                           @PathVariable Integer id_host,
                                           @PathVariable("id_back") Integer backupId) {
        try {
            int operationId = serviceBackup.rollbackBackup(id_host, backupId, request);
            return ResponseEntity.ok(operationId);
        } catch (Exception e) {
			System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id_host}/{id_back}")
    @AllowedOrigins(MService.WEB)
    public ResponseEntity<Integer> deleteBackup(@RequestBody ProtectedRequest<Void> request,
                                               @PathVariable Integer id_host,
                                               @PathVariable("id_back") Integer backupId) {
        try {
            int operationId = serviceBackup.deleteBackup(id_host, backupId, request);
            return ResponseEntity.ok(operationId);
        } catch (Exception e) {
			System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/get_status/{id_operation}")
    @AllowedOrigins(MService.WEB)
    public ResponseEntity<String> getOperationStatus(@RequestBody ProtectedRequest<Void> request,
                                                     @PathVariable Integer id_operation) {
        String status = serviceBackup.getOperationStatus(id_operation);
        return status.equals("not found") 
            ? ResponseEntity.notFound().build() 
            : ResponseEntity.ok(status);
    }
}