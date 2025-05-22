package com.cubes_and_mods.host.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.host.docker.DockerContainerHandler;
import com.cubes_and_mods.host.jpa.Backup;
import com.cubes_and_mods.host.jpa.repos.BackupRepos;
import com.cubes_and_mods.host.jpa.repos.HostRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;

@Service
public class ServiceBackup {

    private final HostRepos hostRepos;
    
    @Autowired
    private BackupRepos backupRepos;

    @Autowired
    private ServiceDockerContainersHandlers serviceDockerContainersHandlers;

    private final ConcurrentHashMap<Integer, String> backupOperations = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();


    ServiceBackup(HostRepos hostRepos) {
        this.hostRepos = hostRepos;
    }

    
    public List<Backup> getAllBackupsForHost(Integer idHost) {
        System.err.println("вхождение в getAllBackupsForHost");
        return backupRepos.findAll().stream()
                .filter(x -> x.getHostBackup().getId().equals(idHost))
                .toList();
    }

    public Integer createBackup(Integer idHost, String backupName, ProtectedRequest<?> request) throws Exception {
        System.out.println("Получение контейнера");
        var container = serviceDockerContainersHandlers.getContainer(idHost, request);
        System.out.println("Создание ID операции");
        int operationId = random.nextInt();
        System.out.println("Запуск операции создания");
        new Thread(() -> processBackupCreation(operationId, idHost, backupName, container)).start();
        
        return operationId;
    }

    private void processBackupCreation(int operationId, Integer idHost, String backupName, DockerContainerHandler container) {
        try {
            if (!container.containerManager.containerLaunched()) {
                container.containerManager.launchContainer();
            }

            Backup backup = new Backup();
            backup.setHostBackup(hostRepos.findById(idHost).get());
            backup.setName(backupName);
            backup.setCreatedAt(LocalDateTime.now());

            updateOperationStatus(operationId, "making archive");
            long size = container.fileManager.makeBackup(backup);
            backup.setSizeKb(size);

            updateOperationStatus(operationId, "saving to db");
            backupRepos.saveAndFlush(backup);

            updateOperationStatus(operationId, "success");
        } catch (Exception e) {
            handleOperationError(operationId, e);
        }
    }

    public Integer rollbackBackup(Integer idHost, Integer backupId, ProtectedRequest<?> request) throws Exception {
        var container = serviceDockerContainersHandlers.getContainer(idHost, request);
        int operationId = random.nextInt();
        
        new Thread(() -> processRollback(operationId, backupId, container)).start();
        
        return operationId;
    }

    private void processRollback(int operationId, Integer backupId, DockerContainerHandler container) {
        try {
            if (!container.containerManager.containerLaunched()) {
                container.containerManager.launchContainer();
            }

            updateOperationStatus(operationId, "finding backup");
            Backup backup = backupRepos.findById(backupId).orElseThrow();

            updateOperationStatus(operationId, "rolling back");
            container.fileManager.rollbackToBackup(backup);

            updateOperationStatus(operationId, "success");
        } catch (Exception e) {
            handleOperationError(operationId, e);
        }
    }

    public Integer deleteBackup(Integer idHost, Integer backupId, ProtectedRequest<?> request) throws Exception {
        var container = serviceDockerContainersHandlers.getContainer(idHost, request);
        int operationId = random.nextInt();
        
        new Thread(() -> processDeletion(operationId, backupId, container)).start();
        
        return operationId;
    }

    private void processDeletion(int operationId, Integer backupId, DockerContainerHandler container) {
        try {
            if (!container.containerManager.containerLaunched()) {
                container.containerManager.launchContainer();
            }

            updateOperationStatus(operationId, "finding backup");
            Backup backup = backupRepos.findById(backupId).orElseThrow();

            updateOperationStatus(operationId, "deleting backup");
            container.fileManager.deleteBackup(backup);

            updateOperationStatus(operationId, "removing from db");
            backupRepos.deleteById(backupId);
            backupRepos.flush();

            updateOperationStatus(operationId, "success");
        } catch (Exception e) {
            handleOperationError(operationId, e);
        }
    }

    public String getOperationStatus(Integer operationId) {
        return backupOperations.getOrDefault(operationId, "not found");
    }

    private void updateOperationStatus(int operationId, String status) {
        backupOperations.put(operationId, status);
    }

    private void handleOperationError(int operationId, Exception e) {
        String errorMessage = "error at " + backupOperations.getOrDefault(operationId, "unknown step") + ": " + e.getMessage();
        backupOperations.put(operationId, errorMessage);
        e.printStackTrace();
    }
}