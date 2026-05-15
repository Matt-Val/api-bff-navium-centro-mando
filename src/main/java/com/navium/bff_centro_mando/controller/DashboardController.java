package com.navium.bff_centro_mando.controller;
import com.navium.bff_centro_mando.service.CentroMandoService;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Operaciones del centro de mando")
public class DashboardController {
    
    // Servicio de negocio que arma la vista del dashboard.
    private final CentroMandoService service;

    // Inyecta el servicio para orquestar llamadas a microservicios.
    public DashboardController(CentroMandoService service) { 
        this.service = service;
    }

    // Endpoint principal del dashboard con el estado de las operaciones.
    @GetMapping("/operaciones")
    @Operation(summary = "Ver tablero", description = "Obtiene el estado de las operaciones del dashboard")
    public ResponseEntity<List<DashboardOperacionResponse>> verTablero() { 
        return ResponseEntity.ok(service.obtenerTableroPrincipal());
    }
}
