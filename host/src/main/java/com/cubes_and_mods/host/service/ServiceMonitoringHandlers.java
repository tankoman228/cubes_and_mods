package com.cubes_and_mods.host.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cubes_and_mods.host.docker.DockerContainerHandler;
import com.cubes_and_mods.host.jpa.repos.HostRepos;

@Service
public class ServiceMonitoringHandlers {

    @Autowired
    private HostRepos hostRepos;


    @Scheduled(fixedDelay = 5000) 
    @Transactional
    public void task() {
        try {
            System.out.println("Monitoring containers: " + ServiceDockerContainersHandlers.handlers.size());
            for (var handler : ServiceDockerContainersHandlers.handlers.values()) {
                if (checkContainer(handler)) {    
                    if (handler.containerManager.containerCreated()) {
                        handler.processManager.killGameServer();
                        handler.containerManager.killContainer();
                    }                        
                }
            }
        } catch (Exception e) {
            System.out.println("Error in monitoring task");
            e.printStackTrace();
        }
    }

    private boolean checkContainer(DockerContainerHandler handler) throws Exception { 

        var previousTariff = handler.host.getTariffHost();
        handler.host = hostRepos.findById(handler.host.getId()).get();

        var tariffNew = handler.host.getTariffHost();

        if (!tariffNew.getId().equals(previousTariff.getId()) || handler.host.getSecondsWorking() < 600) {
            handler.containerManager.updateResourceLimits(tariffNew.getRam() * 1024 * 1024 * 1024L, (long)tariffNew.getCpuThreads());
        }

        var disk = handler.containerManager.getContainerDiskUsageKb();
        if (disk > tariffNew.getMemoryLimit()) {
            return false;
        }

        handler.host.setMemoryUsed(disk);
        handler.host.setSecondsWorking(handler.host.getSecondsWorking() + handler.containerManager.getRuntimeSecondsAfterPreviousCall());
        hostRepos.saveAndFlush(handler.host);
        
        return handler.host.getSecondsWorking() * 3600 < tariffNew.getHoursWorkMax();
    }   
}
