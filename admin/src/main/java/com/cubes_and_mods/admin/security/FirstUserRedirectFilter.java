package com.cubes_and_mods.admin.security;

import java.io.IOException;

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

    private final AdminRepos userRepository;

    public FirstUserRedirectFilter(AdminRepos userRepository2) {
        this.userRepository = userRepository2;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Пропускаем статические ресурсы, страницы и API-запросы
        if (pathMatcher.match("/public/**", requestURI) || 
            pathMatcher.match("/login", requestURI) || 
            pathMatcher.match("/first_register", requestURI) ||
            pathMatcher.match("/verify_ssl", requestURI) || 
            pathMatcher.match("/api/loginn/**", requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean hasUsers = userRepository.count() > 0;
        if (!hasUsers && !pathMatcher.match("/first_register", requestURI)) {
            response.sendRedirect("/first_register");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
