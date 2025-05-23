package com.cubes_and_mods.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cubes_and_mods.admin.jpa.Admin;
import com.cubes_and_mods.admin.jpa.repos.AdminRepos;

/*
 * Регистрация и авторизация пользователей
*/
@Service
public class ServiceLogin {

    @Autowired
    private AdminRepos userRepository;

    @Autowired
    private PasswordHash PasswordHash;

    public HttpStatus login(Admin User) { 
        
        var user = userRepository.findByEmail(User.getUsername());
        if (user.isEmpty()) return HttpStatus.NOT_FOUND;
        
        if (!PasswordHash.checkHash(user.get().getPasswordHash(), User.getPasswordHash(), (int)user.get().getId())) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.OK;
    }

    /**
     * Первая регистрация первого админа. 
     * @return возвращает ошибку или null, если всё ОК
     */
    @Transactional
    public String firstRegister(Admin user) { 

        if (userRepository.findAll().size() > 0) 
            return "Уже есть как минимум 1 пользователь, если это не вы, срочно обратитесь к администратору, кто-то захватил полный доступ к системе раньше вас";

        try {

            userRepository.save(user);
            userRepository.flush();
            
            user.setPasswordHash(PasswordHash.hash(user.getPasswordHash(), user.getId()));

            userRepository.save(user);
            userRepository.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Ошибка сервера: " + e.getClass().getName();
        }

        return null;
    }
}