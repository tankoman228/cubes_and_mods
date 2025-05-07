package com.cubes_and_mods.host.docker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.cubes_and_mods.host.jpa.Host;
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
    
    public ContainerManager(DockerClient client, Host host) {
        this.client = client;
        this.host = host;
        this.containerName = "mc-container-" + host.getId();
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
    }

    public void launchContainer() throws InterruptedException {
        if (!containerCreated()) {
            createContainer();
        }
        if (!containerLaunched()) {
            client.startContainerCmd(containerName).exec();

            // Wait until Docker reports container is running
            waitForCondition(this::containerLaunched, 10, 1000);
        }

        // Prepare and execute SSH install script synchronously
        String[] installCmd = new String[] {
            "bash", "-c",
            "apt update && \\" +
            "apt install -y openssh-server && \\" +
            "mkdir -p /var/run/sshd && \\" +
            "echo 'root:password1488' | chpasswd && \\" +
            "sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \\" +
            "sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config && \\" +
            "service ssh start"
        };

        // Create and start exec command
        ExecCreateCmdResponse execCreateResponse = client.execCreateCmd(containerName)
            .withCmd(installCmd)
            .withAttachStdout(true)
            .withAttachStderr(true)
            .exec();

        client.execStartCmd(execCreateResponse.getId()).exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onComplete() {
                System.out.println("Install script completed successfully");
            }
        }).awaitCompletion();
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

    // TODO: последние два метода пока не тыкать, я сервак не настроил, пока это не важно
    public String getGlobalAddress() {
        // Assuming host machine IP, can be configured TODO: разобраться с глобальными адресами
        return "неизвестный адрес ъуъ " + ":" + (22563 + host.getId() * 2);
    }

    public Map<String, String> getSSHandSFTPinfo() {
        Map<String, String> info = new HashMap<>();
        info.put("host", "неизвестный адрес ъуъ");
        info.put("port", String.valueOf(22563 + host.getId() * 2));
        info.put("user", "root");
        info.put("password", "password1488");
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
}