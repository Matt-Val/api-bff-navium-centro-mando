package com.navium.bff_centro_mando.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// Configuracion central del RestClient usado por los clientes HTTP.
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(JwtPropagationInterceptor jwtInterceptor) { 
        // Cada vez que se un Client se construya, se le amarra el interceptor de seguridad.
        return RestClient.builder()
            .requestInterceptor(jwtInterceptor);
    }
}
