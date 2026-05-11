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
        // Hace un GET, recibe JSON y lo convierte en una lista tipada.
        return CompletableFuture.supplyAsync( () -> 
            restClient.get()
                .uri("/api/contenedores")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContenedorResponse>>() {})
        );
    }

    // Método fallback que devuelve una lista vacía si el servicio falla o se demora.
    public CompletableFuture<List<ContenedorResponse>> fallbackObtenerTodosLosContenedores(Exception e) { 
        System.out.println("Ms-Contenedores no disponible. Retornando una lista vacía. Error: " + e.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
}
