package com.navium.bff_centro_mando.client;

import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


// Cliente HTTP que consume el microservicio de agendamientos.
@Component
public class AgendamientoClient {
    
    // Cliente configurado con baseUrl y el interceptor JWT.
    private final RestClient restClient;

    // Inyecta el builder configurado en RestClientConfig y define el host del ms-agendamiento.
    public AgendamientoClient(RestClient.Builder builder, @Value("${navium.api.agendamientos.url}") String baseUrl) { 
        // Base URL del microservicio de agendamientos.
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    // Obtiene todos los agendamientos desde el microservicio remoto.
    public List<AgendamientoResponse> obtenerTodosLosAgendamientos() { 
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return restClient.get() // GET Request
                .uri("/api/agendamientos") 
                .retrieve() // Ejecuta la petición
                .body(new ParameterizedTypeReference<List<AgendamientoResponse>>() {}); // Convierte la respuesta a una lista de AgendamientoResponse
    }

    // Obtiene un agendamiento por id desde el microservicio remoto.
    public AgendamientoResponse obtenerAgendamientoPorId(Long id) { 
        // Hace un GET con un id, recibe JSON y lo convierte en un objeto.
        return restClient.get()
                .uri("/api/agendamientos/{id}", id)
                .retrieve()
                .body(AgendamientoResponse.class);
    }
}
