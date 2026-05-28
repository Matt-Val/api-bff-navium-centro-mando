package com.navium.bff_centro_mando.controller;
import com.navium.bff_centro_mando.service.CentroMandoService;
import com.navium.bff_centro_mando.web.dto.AndenVistaResponse;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import com.navium.bff_centro_mando.web.dto.DocumentoRevisionResponse;
import com.navium.bff_centro_mando.web.dto.EstadisticasDashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/mapa-andenes")
    @Operation(summary = "Ver mapa de andenes", description = "Obtiene la disposición espacial de los andenes y su ocupación")
    public ResponseEntity<List<AndenVistaResponse>> verMapa() {
        return ResponseEntity.ok(service.obtenerMapaAndenes());
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Ver estadísticas", description = "Obtiene métricas agregadas para gráficos")
    public ResponseEntity<EstadisticasDashboardResponse> verEstadisticas() {
        return ResponseEntity.ok(service.obtenerEstadisticas());
    }

    @GetMapping("/documentos/pendientes")
    @Operation(summary = "Documentos pendientes", description = "Lista documentos que requieren revisión manual")
    public ResponseEntity<List<DocumentoRevisionResponse>> verDocumentos() {
        return ResponseEntity.ok(service.obtenerDocumentosPendientes());
    }

    @PostMapping("/documentos/{idContenedor}/revisar")
    @Operation(summary = "Revisar documento", description = "Aprueba o rechaza la documentación de un contenedor")
    public ResponseEntity<String> revisarDocumento(
            @PathVariable String idContenedor,
            @RequestParam String estado) {
        // Lógica simulada de aprobación
        return ResponseEntity.ok("Estado del contenedor " + idContenedor + " actualizado a " + estado);
    }
}
