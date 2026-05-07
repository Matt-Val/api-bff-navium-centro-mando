package com.navium.bff_centro_mando.controller;
import com.navium.bff_centro_mando.service.CentroMandoService;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v0/dashboard")
public class DashboardController {
    
    // el Final garantiza que el campo no puede ser modificado.
    private final CentroMandoService service;


    
    public DashboardController(CentroMandoService service) { 
        this.service = service;
    }

    @GetMapping("/operaciones")
    public ResponseEntity<List<DashboardOperacionResponse>> verTablero() { 
        return ResponseEntity.ok(service.obtenerTableroPrincipal());
    }
}
