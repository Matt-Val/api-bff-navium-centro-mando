package com.navium.bff_centro_mando.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;

import com.navium.bff_centro_mando.client.dto.ContenedorResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


// Cliente HTTP que consume el microservicio de contenedores.
@Component
public class ContenedoresClient {

    // Cliente configurado con baseUrl y el interceptor JWT.
    private final RestClient restClient;
    
    // Inyecta el builder configurado en RestClientConfig y define el host del ms-contenedores.
    public ContenedoresClient(RestClient.Builder builder, @Value("${navium.api.contenedores.url}") String baseUrl) { 
        this.restClient = builder.baseUrl(baseUrl).build();
    }


    @CircuitBreaker(name = "servicioContenedores", fallbackMethod = "fallbackObtenerTodosLosContenedores")
    @TimeLimiter(name = "servicioContenedores", fallbackMethod = "fallbackObtenerTodosLosContenedores")

    // Obtiene todos los contenedores desde el microservicio remoto.
    public CompletableFuture<List<ContenedorResponse>> obtenerTodosLosContenedores() { 
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return CompletableFuture.supplyAsync( () -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/contenedores")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {});
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    // Método fallback que devuelve una lista vacía si el servicio falla o se demora.
    public CompletableFuture<List<ContenedorResponse>> fallbackObtenerTodosLosContenedores(Exception e) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @CircuitBreaker(name = "servicioContenedores", fallbackMethod = "fallbackActualizarEstado")
    @TimeLimiter(name = "servicioContenedores", fallbackMethod = "fallbackActualizarEstado")
    public CompletableFuture<Void> actualizarEstado(Long id, String estadoBL, String estadoTATC) {
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.runAsync(() -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                restClient.put()
                    .uri(uriBuilder -> uriBuilder
                        .path("/api/contenedores/{id}/estado")
                        .queryParam("estadoBL", estadoBL)
                        .queryParam("estadoTATC", estadoTATC)
                        .build(id))
                    .retrieve()
                    .toBodilessEntity();
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    public CompletableFuture<Void> fallbackActualizarEstado(Long id, String estadoBL, String estadoTATC, Exception e) {
        throw new RuntimeException("No se pudo actualizar el estado legal en el microservicio: " + e.getMessage());
    }
}
