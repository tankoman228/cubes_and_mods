package com.cubes_and_mods.host.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.host.jpa.repos.VersionRepos;
import com.cubes_and_mods.host.security.ProtectedRequest;

/**
 * Для контроллера игровых серверов. Проксирует запросы до докер-контейнеров
 */
@Service
public class ServiceGame {

        // Да твою мышь, автоформат ещё хуже сделал!!

        @Autowired
        private ServiceDockerContainersHandlers serviceContainersHandlers;

        @Autowired
        private VersionRepos versionRepos;

        public boolean is_alive(ProtectedRequest<Void> request, Integer id) throws Exception {

                var c = serviceContainersHandlers.getContainer(id, request);

                if (!c.containerManager.containerCreated()) return false;
                return c.processManager.isGameServerAlive();
        }

        public void launch(ProtectedRequest<Void> request, Integer id) throws Exception {

                var c = serviceContainersHandlers.getContainer(id, request);

                // Нельзя запустить, если игра не установлена!
                if (!installed(request, id)) throw new Exception("Game server not installed");
                
                c.processManager.startGameServer();
        }

        public void kill(ProtectedRequest<Void> request, Integer id) throws Exception {

                var c = serviceContainersHandlers.getContainer(id, request);

                // Игровой сервак не может быть убит, если ему негде быть запущенным
                if (!c.containerManager.containerCreated()) return;
                if (!c.containerManager.containerLaunched()) return;

                c.processManager.killGameServer();
        }

        public void unpack_by_version(ProtectedRequest<Void> request, Integer id, Integer vid) throws Exception {

                // Поиск сервака и версии игры
                var c = serviceContainersHandlers.getContainer(id, request);
                var v = versionRepos.findById(vid).orElseThrow(() -> new Exception("Version not found"));

                // Проверки, чтобы контейнер живой был
                if (!c.containerManager.containerCreated()) c.containerManager.createContainer();
                if (!c.containerManager.containerLaunched()) c.containerManager.launchContainer();

                c.fileManager.initGameServerByVersion(v);
        }

        public boolean installed(ProtectedRequest<Void> request, Integer id) throws Exception {

                var c = serviceContainersHandlers.getContainer(id, request);

                if (!c.containerManager.containerCreated()) return false;
                if (!c.containerManager.containerLaunched()) {
                        c.containerManager.launchContainer(); // Мы должны запустить его, чтобы проверить, что всё работает
                }

                return c.fileManager.isGameServerInstalled();
        }

        public void uninstall(ProtectedRequest<Void> request, Integer id) throws Exception {

                var c = serviceContainersHandlers.getContainer(id, request);

                if (!c.containerManager.containerCreated()) return;
                if (!c.containerManager.containerLaunched()) {
                        c.containerManager.launchContainer(); // Мы должны запустить его, чтобы было, что удалять
                }

                c.containerManager.deleteContainer();
        }
}
