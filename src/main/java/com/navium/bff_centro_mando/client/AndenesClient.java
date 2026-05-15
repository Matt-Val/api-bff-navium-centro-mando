package com.navium.bff_centro_mando.client;


import com.navium.bff_centro_mando.client.dto.AndenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    @CircuitBreaker(name = "servicioAndenes", fallbackMethod = "fallbackObtenerTodos")
    @TimeLimiter(name = "servicioAndenes", fallbackMethod = "fallbackObtenerTodos")
    public CompletableFuture<List<AndenResponse>> obtenerTodos() { 
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return CompletableFuture.supplyAsync( () -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/v0/andenes")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AndenResponse>>() {} );
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    // FallBack 1: Si el servicio de andenes falla o se demora, devuelve una lista vacía.
    public CompletableFuture<List<AndenResponse>> fallbackObtenerTodos(Exception e) { 
        System.out.println("Ms-Andenes no disponible. Retornando una lista vacía. Error: " + e.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @CircuitBreaker(name = "servicioAndenes", fallbackMethod = "fallbackObtenerPorCodigo")
    @TimeLimiter(name = "servicioAndenes", fallbackMethod = "fallbackObtenerPorCodigo")
    public CompletableFuture<AndenResponse> obtenerPorCodigo(String codigo) { 
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        // Hace un GET con el codigo, recibe JSON y lo convierte en un objeto.
        return CompletableFuture.supplyAsync( () -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/v0/andenes/codigo/{codigo}", codigo)
                    .retrieve()
                    .body(AndenResponse.class);
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    // FallBack 2: El método recibe el parámetro 'codigo' y la excepcion
    public CompletableFuture<AndenResponse> fallbackObtenerPorCodigo(String codigo, Exception e) { 
        System.out.println("Ms-Andenes no disponible. No se pudo obtener el anden con codigo: " + codigo + ". Error: " + e.getMessage());
        return CompletableFuture.completedFuture(new AndenResponse());
    }
}
