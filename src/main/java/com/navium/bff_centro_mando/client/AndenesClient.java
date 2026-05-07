package com.navium.bff_centro_mando.client;


import com.navium.bff_centro_mando.client.dto.AndenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

// Cliente HTTP que consume el microservicio de andenes.
@Component
public class AndenesClient {

    // Cliente configurado con baseUrl y el interceptor JWT.
    private final RestClient restClient;

    // Inyecta el builder configurado en RestClientConfig y define el host del ms-andenes.
    public AndenesClient(RestClient.Builder builder, @Value("${navium.api.andenes.url}") String baseUrl) { 
        // Base URL del microservicio de andenes.
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    // Obtiene el listado completo de andenes.
    public List<AndenResponse> obtenerTodos(){ 
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return restClient.get()
                .uri("/api/v0/andenes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AndenResponse>>() {} );
    }

    // Obtiene un anden por su codigo.
    public AndenResponse obtenerPorCodigo(String codigo) { 
        // Hace un GET con el codigo, recibe JSON y lo convierte en un objeto.
        return restClient.get()
                .uri("/api/v0/andenes/codigo/{codigo}", codigo)
                .retrieve()
                .body(AndenResponse.class);
    }
}
