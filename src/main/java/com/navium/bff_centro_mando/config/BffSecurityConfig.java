package com.navium.bff_centro_mando.config;

import com.navium.security_lib.security.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class BffSecurityConfig {
    
    // Importamos el filtro de la librería de seguridad.
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public BffSecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) { 
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    @Order(1) // Asegura que esta configuración se aplique antes que otras configuraciones de seguridad.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Que SOLO active cuando la ruta sea /api/dashboard/**
            .securityMatcher("/api/dashboard/**")
            
            .csrf(csrf -> csrf.disable())
            // No Cookies, Modo Stateless JWT
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // Cualquiera que entre a /api/dashboard/** exige el rol
                .anyRequest().authenticated()
            )
            // Agregamos el filtro para que revise el Token antes dejarles pasar.
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
