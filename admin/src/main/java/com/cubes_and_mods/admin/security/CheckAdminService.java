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

    /**
     * Выбросит исключение 403, если админ не авторизован, либо отсутствуют права из второго аргумента
     */
    public void assertAllowed(HttpServletRequest request, InnerCheckAdminService innerCheckAdminService) {
        var admin = getUser(request);
        if (innerCheckAdminService.check(admin)) {
            return;
        }
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
