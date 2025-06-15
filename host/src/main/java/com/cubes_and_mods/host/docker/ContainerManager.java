package com.cubes_and_mods.host.docker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.cubes_and_mods.host.jpa.Host;
import com.cubes_and_mods.host.security.MicroserviceInitializer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;

/**
 * Управление непосредственно контейнером, создание, остановка
 */
public class ContainerManager {
    
    private final DockerClient client;
    private final Host host;
    private String containerName;
    private DockerContainerHandler handler;
    
    public ContainerManager(DockerClient client, Host host, DockerContainerHandler handler) {
        this.client = client;
        this.host = host;
        this.containerName = "mc-container-" + host.getId();
        this.handler = handler;
    }

    public boolean containerCreated() {
        try {
            client.inspectContainerCmd(containerName).exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public boolean containerLaunched() {
        try {
            InspectContainerResponse container = client.inspectContainerCmd(containerName).exec();
            return container.getState().getRunning();
        } catch (NotFoundException e) {
            return false;
        }
    }

    public void createContainer() throws InterruptedException {

        if (containerCreated()) {
            return;
        }

        int sshPort = 22563 + host.getId() * 3;
        int gamePort = 25565 + host.getId() * 3;
        int auxPort = 25566 + host.getId() * 3;

        System.out.println("Creating container " + containerName);
        System.out.println("SSH port: " + sshPort);
        System.out.println("Game port: " + gamePort);
        System.out.println("Aux port: " + auxPort);

        client.createContainerCmd("pingvin-jdk17")
              .withName(containerName)
              .withExposedPorts(
                    ExposedPort.tcp(22),
                    ExposedPort.tcp(25565),
                    ExposedPort.tcp(25566)
              )
              .withHostConfig(HostConfig.newHostConfig()
                    .withMemory(5L * 1024 * 1024 * 1024)
                    .withCpuCount(4L)
                    .withPortBindings(
                        new PortBinding(Ports.Binding.bindPort(sshPort), ExposedPort.tcp(22)),
                        new PortBinding(Ports.Binding.bindPort(gamePort), ExposedPort.tcp(25565)),
                        new PortBinding(Ports.Binding.bindPort(auxPort), ExposedPort.tcp(25566))
                    )
              )
              .exec();

        // Wait until Docker reports the container exists
        waitForCondition(this::containerCreated, 10, 1000);

        System.out.println("Container creation finished: " + containerName);
    }

    public void launchContainer() throws InterruptedException {
        if (!containerCreated()) {
            createContainer();
        }
        if (!containerLaunched()) {
            client.startContainerCmd(containerName).exec();
    
            // Ждём запуска
            waitForCondition(this::containerLaunched, 10, 1000);
        }
    
        System.out.println("Container launched and SSH server running");
    }


    public void killContainer() {
        if (containerLaunched()) {
            client.killContainerCmd(containerName).exec();
        }
    }

    public void deleteContainer() {
        if (containerCreated()) {
            if (containerLaunched()) killContainer();
            client.removeContainerCmd(containerName).withForce(true).exec();
        }
    }

    public String getGlobalAddress() {
        return MicroserviceInitializer.GLOBAL_ADDRESS + ":" + (22563 + host.getId() * 2);
    }
    public Map<String, String> getSSHandSFTPinfo() {
        Map<String, String> info = new HashMap<>();
        info.put("host", MicroserviceInitializer.GLOBAL_ADDRESS);
        info.put("ssh_port", String.valueOf(22563 + host.getId() * 3));
        info.put("game_port", String.valueOf(25565 + host.getId() * 3));
        info.put("aux_port", String.valueOf(25566 + host.getId() * 3));
        info.put("user", "root");
        info.put("password", "change_me_please_after_order_confirmed");        
        return info;
    }

    /*
     * Чтобы блокировать поток, пока грузится контейнер
     */
    private void waitForCondition(Supplier<Boolean> condition, int maxAttempts, long delayMillis) throws InterruptedException {
        for (int i = 0; i < maxAttempts; i++) {
            if (condition.get()) {
                return;
            }
            Thread.sleep(delayMillis);
        }
        throw new RuntimeException("Timeout waiting for condition");
    }


    /**
     * Изменяет ресурсы уже созданного контейнера
     */
    public void updateResourceLimits(long memoryBytes, long cpuCount) throws InterruptedException {
        if (!containerCreated()) {
            createContainer();
        }
        if (containerLaunched() && handler.processManager.isGameServerAlive()) return;

        InspectContainerResponse containerInfo = client.inspectContainerCmd(containerName).exec();
        HostConfig hostConfig = containerInfo.getHostConfig();
        
        long cpuPeriod = hostConfig.getCpuPeriod() != null ? hostConfig.getCpuPeriod() : 100000L;
        long cpuQuota = cpuCount * cpuPeriod;

        client.updateContainerCmd(containerName)
            .withMemory(memoryBytes)
            .withCpuPeriod((int)cpuPeriod)
            .withCpuQuota((int)cpuQuota)
            .exec();
        
        System.out.printf("Ресурсы контейнера %s обновлены: memory=%d, cpuCount=%d",
                        containerName, memoryBytes, cpuCount);
    }

    private long lastCallTime = System.currentTimeMillis();
    /**
     * Возвращает время с последнего вызова в секундах, вернёт 0, если рантайм в момент измерения нулевой
     */
    public Integer getRuntimeSecondsAfterPreviousCall() {

        long currentTime = System.currentTimeMillis();
        long timeSinceLastCall = currentTime - lastCallTime;
        lastCallTime = currentTime;

        if (!containerCreated()) return 0;
        if (!containerLaunched()) return 0;
        if (!handler.processManager.isGameServerAlive()) return 0;

        return (int) (timeSinceLastCall / 1000);
    }

    public long getContainerDiskUsageKb() {
        try {
            // Обязательно указываем withSize(true), иначе sizeRootFs будет null
            InspectContainerResponse inspect = client.inspectContainerCmd(containerName)
                                                      .withSize(true)
                                                      .exec();
    
            // Размер изменений в файловой системе контейнера
            var sizeRootFs = inspect.getSizeRootFs(); // может быть null
    
            // Размер образа, на котором основан контейнер
            String imageId = inspect.getImageId();
            var image = client.inspectImageCmd(imageId).exec();
            Long imageSize = image.getVirtualSize(); // тоже может быть null
    
            long totalBytes = (sizeRootFs != null ? sizeRootFs : 0L)
                            + (imageSize != null ? imageSize : 0L);
    
            return totalBytes / 1024; // Килобайты
        } catch (NotFoundException e) {
            throw new RuntimeException("Container not found: " + containerName, e);
        }
    }

}