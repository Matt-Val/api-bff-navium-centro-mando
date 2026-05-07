package com.navium.bff_centro_mando.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;

import com.navium.bff_centro_mando.client.dto.ContenedorResponse;

import java.util.List;
// Cliente HTTP que consume el microservicio de contenedores.
@Component
public class ContenedoresClient {

    // Cliente configurado con baseUrl y el interceptor JWT.
    private final RestClient restClient;
    
    // Inyecta el builder configurado en RestClientConfig y define el host del ms-contenedores.
    public ContenedoresClient(RestClient.Builder builder, @Value("${navium.api.contenedores.url}") String baseUrl) { 
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    // Obtiene todos los contenedores desde el microservicio remoto.
    public List<ContenedorResponse> obtenerTodosLosContenedores() { 
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return restClient.get()
                .uri("/api/contenedores")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {});
    }
}
