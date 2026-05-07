package com.navium.bff_centro_mando.client;


import com.navium.bff_centro_mando.client.dto.AndenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AndenesClient {

    private final RestClient restClient;

    public AndenesClient(RestClient.Builder builder) { 
        // Apuntamos hacia ms-andeneros, esperando que a futuro sea el host del servicio de andenes
        this.restClient = builder.baseUrl("http://localhost:8084").build();
    }

    public List<AndenResponse> obtenerTodos(){ 
        return restClient.get()
                .uri("/api/v0/andenes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AndenResponse>>() {} );
    }

    public AndenResponse obtenerPorCodigo(String codigo) { 
        return restClient.get()
                .uri("/api/v0/andenes/codigo/{codigo}", codigo)
                .retrieve()
                .body(AndenResponse.class);
    }
}
