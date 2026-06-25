package com.navium.bff_centro_mando.client;

import com.navium.bff_centro_mando.client.dto.UsuarioRequest;
import com.navium.bff_centro_mando.client.dto.UsuarioResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(RestClient.Builder builder, @Value("${navium.api.usuarios.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "servicioUsuarios", fallbackMethod = "fallbackListarTodos")
    @TimeLimiter(name = "servicioUsuarios", fallbackMethod = "fallbackListarTodos")
    public CompletableFuture<List<UsuarioResponse>> listarTodos() {
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync(() -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.get()
                    .uri("/api/usuarios")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UsuarioResponse>>() {});
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    public CompletableFuture<List<UsuarioResponse>> fallbackListarTodos(Exception e) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @CircuitBreaker(name = "servicioUsuarios", fallbackMethod = "fallbackCrearUsuario")
    @TimeLimiter(name = "servicioUsuarios", fallbackMethod = "fallbackCrearUsuario")
    public CompletableFuture<UsuarioResponse> crearUsuario(UsuarioRequest request) {
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync(() -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.post()
                    .uri("/api/usuarios")
                    .body(request)
                    .retrieve()
                    .onStatus(org.springframework.http.HttpStatusCode::is4xxClientError, (req, resp) -> {
                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(resp.getBody()));
                        String message = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                        throw new RuntimeException(message);
                    })
                    .body(UsuarioResponse.class);
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    public CompletableFuture<UsuarioResponse> fallbackCrearUsuario(UsuarioRequest request, Exception e) {
        // Si el error ya trae un mensaje del microservicio (vía RuntimeException), lo propagamos.
        String errorMsg = e.getMessage();
        if (e instanceof java.util.concurrent.CompletionException && e.getCause() != null) {
            errorMsg = e.getCause().getMessage();
        }
        throw new RuntimeException(errorMsg);
    }

    @CircuitBreaker(name = "servicioUsuarios", fallbackMethod = "fallbackActualizarUsuario")
    @TimeLimiter(name = "servicioUsuarios", fallbackMethod = "fallbackActualizarUsuario")
    public CompletableFuture<UsuarioResponse> actualizarUsuario(Long id, UsuarioRequest request) {
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync(() -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                return restClient.put()
                    .uri("/api/usuarios/{id}", id)
                    .body(request)
                    .retrieve()
                    .onStatus(org.springframework.http.HttpStatusCode::is4xxClientError, (req, resp) -> {
                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(resp.getBody()));
                        String message = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                        throw new RuntimeException(message);
                    })
                    .body(UsuarioResponse.class);
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    public CompletableFuture<UsuarioResponse> fallbackActualizarUsuario(Long id, UsuarioRequest request, Exception e) {
        String errorMsg = e.getMessage();
        if (e instanceof java.util.concurrent.CompletionException && e.getCause() != null) {
            errorMsg = e.getCause().getMessage();
        }
        throw new RuntimeException(errorMsg);
    }

    @CircuitBreaker(name = "servicioUsuarios", fallbackMethod = "fallbackDesactivarUsuario")
    @TimeLimiter(name = "servicioUsuarios", fallbackMethod = "fallbackDesactivarUsuario")
    public CompletableFuture<Void> desactivarUsuario(Long id) {
        var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync(() -> {
            org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
            try {
                restClient.delete()
                    .uri("/api/usuarios/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
                return null;
            } finally {
                org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
            }
        });
    }
public CompletableFuture<Void> fallbackDesactivarUsuario(Long id, Exception e) {
    return CompletableFuture.completedFuture(null);
}

@CircuitBreaker(name = "servicioUsuarios", fallbackMethod = "fallbackActivarUsuario")
@TimeLimiter(name = "servicioUsuarios", fallbackMethod = "fallbackActivarUsuario")
public CompletableFuture<Void> activarUsuario(Long id) {
    var attributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
    return CompletableFuture.supplyAsync(() -> {
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(attributes);
        try {
            restClient.patch()
                .uri("/api/usuarios/{id}/activar", id)
                .retrieve()
                .toBodilessEntity();
            return null;
        } finally {
            org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes();
        }
    });
}

public CompletableFuture<Void> fallbackActivarUsuario(Long id, Exception e) {
    return CompletableFuture.completedFuture(null);
}
}