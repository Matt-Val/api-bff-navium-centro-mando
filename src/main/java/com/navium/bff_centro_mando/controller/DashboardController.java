package com.navium.bff_centro_mando.controller;
import com.navium.bff_centro_mando.client.dto.UsuarioRequest;
import com.navium.bff_centro_mando.client.dto.UsuarioResponse;
import com.navium.bff_centro_mando.service.CentroMandoService;
import com.navium.bff_centro_mando.service.RevisionDocumentalService;
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
    
    private final CentroMandoService service;
    private final RevisionDocumentalService revisionService;

    public DashboardController(CentroMandoService service, RevisionDocumentalService revisionService) { 
        this.service = service;
        this.revisionService = revisionService;
    }

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
        return ResponseEntity.ok(revisionService.obtenerDocumentosPendientes());
    }

    @PostMapping("/documentos/{idContenedor}/revisar")
    @Operation(summary = "Revisar documento", description = "Aprueba o rechaza la documentación de un contenedor")
    public ResponseEntity<String> revisarDocumento(
            @PathVariable String idContenedor,
            @RequestParam String estado) {
        revisionService.revisarDocumento(idContenedor, estado);
        return ResponseEntity.ok("Estado del contenedor " + idContenedor + " actualizado a " + estado);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(service.crearUsuario(request));
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza el nombre y rol de un usuario")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(service.actualizarUsuario(id, request));
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario (soft delete)")
    public ResponseEntity<String> desactivarUsuario(@PathVariable Long id) {
        service.desactivarUsuario(id);
        return ResponseEntity.ok("Usuario desactivado exitosamente");
    }

    @PatchMapping("/usuarios/{id}/activar")
    @Operation(summary = "Reactivar usuario", description = "Reactiva un usuario desactivado")
    public ResponseEntity<String> activarUsuario(@PathVariable Long id) {
        service.activarUsuario(id);
        return ResponseEntity.ok("Usuario reactivado exitosamente");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        // Si el mensaje es una excepcion anidada (por el join() de CompletableFuture)
        if (e.getCause() != null) {
            message = e.getCause().getMessage();
        }
        return ResponseEntity.status(400).body(message);
    }
}
