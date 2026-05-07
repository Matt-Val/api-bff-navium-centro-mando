package com.navium.bff_centro_mando.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(JwtPropagationInterceptor jwtInterceptor) { 
        // Permite crear clientes http personalizados para consumir APIs externas
        return RestClient.builder()
            .requestInterceptor(jwtInterceptor);
    }
}
