package com.cubes_and_mods.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.cubes_and_mods.admin.jpa.Admin;
import com.cubes_and_mods.admin.jpa.repos.AdminRepos;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CheckAdminService {

    @Autowired
    private AdminRepos adminRepository;

    @Autowired
    private LoggerService loggerService;

    /**
     * Выбросит исключение 403, если админ не авторизован, либо отсутствуют права из второго аргумента
     */
    public void assertAllowed(HttpServletRequest request, InnerCheckAdminService innerCheckAdminService) {

        var admin = getUser(request);
        loggerService.simpleLog(admin.getUsername() + " запрашивает " + request.getRequestURI());
        if (innerCheckAdminService.check(admin)) {
            loggerService.simpleLog("Успешно");
            return;
        }
        loggerService.simpleLog("\n\nОтказано! Попытка обойти права доступа!!!!!!!!!!!!\n\n");
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @FunctionalInterface
    public interface InnerCheckAdminService {
        boolean check(Admin admin);
    }

    public Admin getUser(HttpServletRequest request) {
        try {
            return adminRepository.findById((int)request.getSession().getAttribute("userId")).get();
        }
        catch (Exception e) {
            System.out.println(e.getClass().getName());
            loggerService.simpleLog("Код 401 при " + request.getRequestURI() + " из-за " + e.getClass().getName());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
