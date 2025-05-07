package com.cubes_and_mods.host.docker;

import java.util.function.Consumer;

import com.cubes_and_mods.host.jpa.Host;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.api.async.ResultCallback;
import java.io.*;

/**
 * Всё, что связано с запуском игры, процессами внутри контейнера, т.е. процессом игрового сервера
 */
public class ProcessManager {
    
    private final DockerClient client;
    private final String containerName;

    public ProcessManager(DockerClient client, Host host) {
        this.client = client;
        this.containerName = "mc-container-" + host.getId();
    }

    public boolean isGameServerAlive() {
        ExecCreateCmdResponse cmd = client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "pgrep -f run.sh")
            .withAttachStdout(true)
            .exec();
        // parse output
        return false;
    }

    public void subscribeToGameserverConsoleOutput(Consumer<String> consumer) {
        LogContainerCmd logCmd = client.logContainerCmd(containerName)
            .withStdOut(true)
            .withStdErr(true)
            .withFollowStream(true)
            .withTailAll();

        logCmd.exec(new ResultCallback<Frame>() {
            @Override
            public void onNext(Frame object) {
                consumer.accept(new String(object.getPayload()));
            }

            @Override
            public void onStart(Closeable closeable) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void close() throws IOException {
            }
        });
    }

    public void killGameServer() {
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "pkill -f run.sh")
            .exec();
    }

    public void startGameServer() {
        client.execCreateCmd(containerName)
            .withCmd("bash", "-c", "cd /game && bash run.sh")
            .withAttachStdout(true)
            .withAttachStderr(true)
            .withTty(true)
            .exec();
    }
}
