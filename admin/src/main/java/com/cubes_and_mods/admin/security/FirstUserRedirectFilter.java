package com.cubes_and_mods.admin.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cubes_and_mods.admin.jpa.repos.AdminRepos;
import com.cubes_and_mods.admin.jpa.repos.ClientRepos;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirstUserRedirectFilter extends OncePerRequestFilter {

    @Autowired
    private AdminRepos userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        System.out.println("FirstUserRedirectFilter: " + request.getRequestURI());

        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Пропускаем статические ресурсы, страницы и API-запросы
        if (
            requestURI.startsWith("/public/") ||
            requestURI.equals("/login") ||
            requestURI.equals("/first_register") ||
            requestURI.equals("/verify_ssl") ||
            requestURI.startsWith("/api/loginn/")
        ) {
            System.out.println("FirstUserRedirectFilter ignore");
            filterChain.doFilter(request, response);
            return;
        }

        boolean hasUsers = userRepository.count() > 0;
        if (!hasUsers && !pathMatcher.match("/first_register", requestURI)) {
            response.sendRedirect("/first_register");
            System.out.println("redirect");
            return;
        }
        System.out.println("FirstUserRedirectFilter login");

        filterChain.doFilter(request, response);
    }
}
