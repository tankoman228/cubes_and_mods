package com.cubes_and_mods.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cubes_and_mods.admin.jpa.repos.AdminRepos;
import com.cubes_and_mods.admin.jpa.repos.ClientRepos;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AdminRepos userRepository;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/public/**", "/api/loginn/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var h = http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/first_register", "/public/**", "/api/loginn/**", "/verify_ssl").permitAll()  // Разрешаем доступ к этим страницам без авторизации
                .anyRequest().authenticated() // Остальные требуют авторизации
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", false) // После логина редиректим на главную, если нет другого URL в сессии
                .successHandler((request, response, authentication) -> {
                    String targetUrl = request.getSession().getAttribute("redirectAfterLogin") != null
                            ? request.getSession().getAttribute("redirectAfterLogin").toString()
                            : "/";
                    
                    // Не перенаправлять, если targetUrl - это страница логина или регистрации
                    if (targetUrl.equals("/login") || targetUrl.equals("/first_register")) {
                        targetUrl = "/";
                    }
                    response.sendRedirect(targetUrl);
                })
            )
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .csrf().disable();

         http.addFilterBefore(new FirstUserRedirectFilter(userRepository), UsernamePasswordAuthenticationFilter.class);
    
        return h.build();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminRepos userRepository) {
        return username -> userRepository.findByEmail(username)
                .<UserDetails>map(user -> User.builder()
                        .username(user.getUsername() ) // Используем email как логин
                        .password(user.getPasswordHash()) // Пароль должен быть закодирован
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
