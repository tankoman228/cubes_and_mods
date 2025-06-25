package com.cubes_and_mods.auth.service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.auth.jpa.Client;
import com.cubes_and_mods.auth.jpa.repos.ClientRepos;

/**
 * Отвечает за логику регистрации и авторизации, с сессиями пользователей не связана, см. логику контроллера
 */
@Service
public class ServiceLoginRegister {

    @Autowired
    private ClientRepos clientRepos;

    @Autowired
    private PasswordHash passwordHash;

    private static volatile ConcurrentHashMap<String, Client> registerRequests = new ConcurrentHashMap<>();


    public Client login(Client clientGot) {
        
        try {
            var clientReal = clientRepos.findByEmail(clientGot.getEmail());
            var hashExpected = passwordHash.hash(clientGot.getPassword(), clientReal.getId());

            if (hashExpected.equals(clientReal.getPassword())) {
                return clientReal;
            }
            return null;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String requireRegisterGetCode(Client client) { 

        if (clientRepos.findByEmail(client.getEmail()) != null) {
            return null;
        }

        // Генерация кода подтверждения
        SecureRandom random = new SecureRandom();
        int code_salt = random.nextInt(9000) + 1000;
        String code = passwordHash.hash(client.getEmail(), code_salt);

        client.setId(null); // Чтобы save в базу данных был только как insert, атака по ID очень опасна
        client.setBanned(false);
        client.setAdditionalInfo("no info");
        
        registerRequests.put(code, client);

        return code;
    }

    /*public Client confirmByCode(String code) {
        
        if (!registerRequests.containsKey(code)) {
            return null;
        }
        Client client = registerRequests.get(code);
        String email = client.getEmail();

        var cl = clientRepos.findAll().stream().filter(x -> x.getEmail().equals(email)).findFirst();
        if (cl.isPresent()) {
            var client2 = cl.get();
            client2.setPassword(client.getPassword());
            client = client2;
        }
        clientRepos.save(client);

        // Хэшируем прароль
        clientRepos.flush();
        client.setPassword(passwordHash.hash(client.getPassword(), client.getId()));
        clientRepos.save(client);
        clientRepos.flush();

        registerRequests.remove(code);
        return client;
    }*/

    public Client confirmByCode(String code) {
        
        if (!registerRequests.containsKey(code)) {
            return null;
        }
        Client client = registerRequests.get(code);
        String email = client.getEmail();

        var cl = clientRepos.findAll().stream().filter(x -> x.getEmail().equals(email)).findFirst();
        if (cl.isPresent()) {
            var client2 = cl.get();
            client2.setPassword(client.getPassword());
            client = client2;
        }
        clientRepos.save(client);

        // Хэшируем прароль
        clientRepos.flush();
        client.setPassword(passwordHash.hash(client.getPassword(), client.getId()));
        clientRepos.save(client);
        clientRepos.flush();

        registerRequests.remove(code);
        return client;
    }

    public String changePasswordGetCode(Client client) { 

        if (clientRepos.findByEmail(client.getEmail()) == null) {
            return null;
        }

        // Генерация кода подтверждения
        SecureRandom random = new SecureRandom();
        int code_salt = random.nextInt(9000) + 1000;
        String code = passwordHash.hash(client.getEmail(), code_salt);

        registerRequests.put(code, client);

        return code;
    }
}
