package com.navium.bff_centro_mando.client;

import com.navium.bff_centro_mando.client.dto.AgendamientoRequest;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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

    @CircuitBreaker(name = "servicioAgendamiento", fallbackMethod = "fallbackObtenerTodos")
    @TimeLimiter(name = "servicioAgendamiento", fallbackMethod = "fallbackObtenerTodos")
    public CompletableFuture<List<AgendamientoResponse>> obtenerTodosLosAgendamientos() { 
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync( () -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/agendamientos") 
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<AgendamientoResponse>>() {});
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    // Fallback 1: Si falla el obtenerTodos
    public CompletableFuture<List<AgendamientoResponse>> fallbackObtenerTodos(Exception e) { 
        // Se mostrará una lista vacía en el dashboard, pero el sistema seguirá funcionando.
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    // Obtiene un agendamiento por id desde el microservicio remoto.
    @CircuitBreaker(name = "servicioAgendamiento", fallbackMethod = "fallbackObtenerPorId")
    @TimeLimiter(name = "servicioAgendamiento", fallbackMethod = "fallbackObtenerPorId")
    public CompletableFuture<AgendamientoResponse> obtenerAgendamientoPorId(Long id) { 
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        // Hace un GET con un id, recibe JSON y lo convierte en un objeto.
        return CompletableFuture.supplyAsync( () -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/agendamientos/{id}", id)
                    .retrieve()
                    .body(AgendamientoResponse.class);
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    // Fallback 2: Si falla el obtenerPorId
    public CompletableFuture<AgendamientoResponse> fallbackObtenerPorId(Long id, Exception e) { 
        // Retornamos un objeto vacío para que la interfaz pueda manejarlo sin romperse.
        return CompletableFuture.completedFuture(new AgendamientoResponse());
    }
}

