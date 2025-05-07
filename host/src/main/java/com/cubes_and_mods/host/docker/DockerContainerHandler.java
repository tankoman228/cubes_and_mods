package com.cubes_and_mods.host.docker;

import com.cubes_and_mods.host.jpa.Host;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
/**
 * Центральный класс, который используется службами. Логика разнесена на 3 компонента. 
 * Всё привязано к одному докер-контейнеру, будь он запущен, создан или нет
 */
public class DockerContainerHandler {

    /**
     * Управление непосредственно контейнером, создание, остановка
     */
    public ContainerManager containerManager;

    /**
     * Всё, что связано с файлами внутри контейнера для игрового сервера
     */
    public FileManager fileManager;

    /**
     * Всё, что связано с запуском игры, процессами внутри контейнера, т.е. процессом игрового сервера
     */
    public ProcessManager processManager;

    /**
     * Хост для игрового сервера из БД
     */
    public Host host;

    public DockerContainerHandler(Host host) {
        this.host = host;


        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")  // Пример: "unix:///var/run/docker.sock" или "tcp://127.0.0.1:2375"
            .build();

        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();

        DockerClient client = DockerClientBuilder.getInstance(config)
            .withDockerHttpClient(httpClient)
            .build();

        this.containerManager = new ContainerManager(client, host);
        this.fileManager = new FileManager(client, host);
        this.processManager = new ProcessManager(client, host);
    }
}
