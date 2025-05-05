package com.cubes_and_mods.host.docker;

import java.util.HashMap;
import java.util.Map;

import com.cubes_and_mods.host.jpa.Host;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;

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

    public void createContainer() {
        if (containerCreated()) return;
        int sshPort = 22563 + host.getId() * 2;
        int gamePort = 25565 + host.getId() * 2;
        int auxPort = 25566 + host.getId() * 2;

        CreateContainerResponse response = client.createContainerCmd("pingvin-jdk17")
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
    }

    public void launchContainer() {
        if (!containerCreated()) createContainer();
        if (!containerLaunched()) client.startContainerCmd(containerName).exec();

        // Install SSH and start it
        String cmd = String.join(" && ",
            "apt update",
            "apt install -y openssh-server",
            "mkdir -p /var/run/sshd",
            "echo 'root:password1488' | chpasswd",
            "sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config",
            "sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config",
            "service ssh start"
        );
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", cmd)
            .exec();
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
        // Assuming host machine IP, can be configured
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
}