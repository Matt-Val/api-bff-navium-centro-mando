package com.navium.bff_centro_mando.client;

import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
public class AgendamientoClient {
    private final RestClient restClient;

    public AgendamientoClient(RestClient.Builder builder) { 
        // Apuntamos hacia ms-agendamiento, esperando que a futuro sea el host del servicio de agendamiento
        this.restClient = builder.baseUrl("http://localhost:8083").build();
    }

    public List<AgendamientoResponse> obtenerTodosLosAgendamientos() { 
        return restClient.get()
                .uri("/api/agendamientos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AgendamientoResponse>>() {});
    }

    public AgendamientoResponse obtenerAgendamientoPorId(Long id) { 
        return restClient.get()
                .uri("/api/agendamientos/{id}", id)
                .retrieve()
                .body(AgendamientoResponse.class);
    }
}
