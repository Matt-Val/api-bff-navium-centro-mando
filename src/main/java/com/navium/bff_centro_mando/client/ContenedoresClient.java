package com.navium.bff_centro_mando.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.navium.bff_centro_mando.client.dto.ContenedorResponse;

import java.util.List;
@Component
public class ContenedoresClient {

    private final RestClient restClient;
    
    public ContenedoresClient(RestClient.Builder builder) { 
        this.restClient = builder.baseUrl("http://localhost:8080").build();
    }

    public List<ContenedorResponse> obtenerTodosLosContenedores() { 
        return restClient.get()
                .uri("/api/contenedores")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {});
    }
}
