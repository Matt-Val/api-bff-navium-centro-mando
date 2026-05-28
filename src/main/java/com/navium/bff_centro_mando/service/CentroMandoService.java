package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.AndenesClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import com.navium.bff_centro_mando.client.dto.AndenResponse;
import com.navium.bff_centro_mando.client.dto.ContenedorResponse;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import com.navium.bff_centro_mando.web.dto.AndenVistaResponse;
import com.navium.bff_centro_mando.web.dto.DocumentoRevisionResponse;
import com.navium.bff_centro_mando.web.dto.EstadisticasDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Servicio que orquesta datos de varios microservicios para el dashboard.
@Service
public class CentroMandoService {
    
    // Cliente para obtener turnos agendados.
    private final AgendamientoClient agendamientoClient;
    // Cliente para obtener estados de contenedores.
    private final ContenedoresClient contenedoresClient;
    // Cliente para obtener andenes.
    private final AndenesClient andenesClient;


    // Inyecta los clientes necesarios para construir la vista del dashboard.
    public CentroMandoService(
        AgendamientoClient agendamientoClient, 
        ContenedoresClient contenedoresClient,
        AndenesClient andenesClient) { 
            this.agendamientoClient = agendamientoClient;
            this.contenedoresClient = contenedoresClient;
            this.andenesClient = andenesClient;
    }


    // Construye la lista de operaciones combinando agendamientos y contenedores.
    public List<DashboardOperacionResponse> obtenerTableroPrincipal() {
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos().join();
        List<ContenedorResponse> todosLosContenedores = contenedoresClient.obtenerTodosLosContenedores().join();
        List<AndenResponse> todosLosAndenes = andenesClient.obtenerTodos().join();

        return agendamientos.stream().map(turno -> {

            // 1. Lógica del Contenedor
            // Buscamos si existe información adicional del contenedor en el ms-contenedores
            String estadoContenedorReal = "NO ENCONTRADO EN PATIO";
            if (turno.getIdContenedor() != null) {
                estadoContenedorReal = todosLosContenedores.stream()
                    .filter(c -> c.getCodigoSigla().equalsIgnoreCase(turno.getIdContenedor()))
                    .map(ContenedorResponse::getEstadoGeneral)
                    .findFirst()
                    .orElse("NO ENCONTRADO EN PATIO");
            }

            // 2. Lógica del Andén
            // Si el agendamiento tiene un código, lo mostramos. Si además existe en el ms-andenes, agregamos la zona.
            String nombreAnden = (turno.getCodigoAnden() != null) ? turno.getCodigoAnden() : "SIN ANDEN ASIGNADO";
            if (turno.getCodigoAnden() != null) {
                String infoExtraAnden = todosLosAndenes.stream()
                    .filter(a -> a.getCodigo().equalsIgnoreCase(turno.getCodigoAnden()))
                    .map(a -> " (Zona " + a.getZona() + ")")
                    .findFirst()
                    .orElse("");
                nombreAnden += infoExtraAnden;
            }

            return DashboardOperacionResponse.builder()
                .idTurno(turno.getId())
                .patenteCamion(turno.getPatenteCamion())
                .horaAgendada(turno.getHoraInicio())
                .tipoOperacion(turno.getTipoOperacion())
                .codigoContenedor(turno.getIdContenedor() != null ? turno.getIdContenedor() : "--")
                .estadoContenedor(estadoContenedorReal)
                .andenAsignado(nombreAnden)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene la vista espacial de los andenes con la información de quién los ocupa.
     */
    public List<AndenVistaResponse> obtenerMapaAndenes() {
        List<AndenResponse> andenes = andenesClient.obtenerTodos().join();
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos().join();

        return andenes.stream().map(anden -> {
            // Buscamos si hay un agendamiento activo asociado a este andén
            AgendamientoResponse ocupante = agendamientos.stream()
                .filter(ag -> anden.getCodigo().equalsIgnoreCase(ag.getCodigoAnden()))
                .findFirst()
                .orElse(null);

            return AndenVistaResponse.builder()
                .id(anden.getId())
                .zona(anden.getZona())
                .numero(anden.getNumero())
                .codigo(anden.getCodigo())
                .tipo(anden.getTipo())
                .estado(anden.getEstado())
                .sector(anden.getSector())
                .patenteOcupante(ocupante != null ? ocupante.getPatenteCamion() : null)
                .contenedorOcupante(ocupante != null ? ocupante.getIdContenedor() : null)
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Genera estadísticas agregadas para alimentar los gráficos del dashboard.
     */
    public EstadisticasDashboardResponse obtenerEstadisticas() {
        List<DashboardOperacionResponse> tablero = obtenerTableroPrincipal();

        Map<String, Long> distribucionPorEstado = tablero.stream()
            .collect(Collectors.groupingBy(DashboardOperacionResponse::getEstadoContenedor, Collectors.counting()));

        Map<String, Long> operacionesPorTipo = tablero.stream()
            .collect(Collectors.groupingBy(DashboardOperacionResponse::getTipoOperacion, Collectors.counting()));

        long enPatio = distribucionPorEstado.getOrDefault("EN PATIO", 0L);
        long perdidos = distribucionPorEstado.getOrDefault("NO ENCONTRADO EN PATIO", 0L);

        return EstadisticasDashboardResponse.builder()
            .totalAgendados(tablero.size())
            .contenedoresEnPatio(enPatio)
            .contenedoresPerdidos(perdidos)
            .distribucionPorEstado(distribucionPorEstado)
            .operacionesPorTipo(operacionesPorTipo)
            .build();
    }

    /**
     * Simula la obtención de documentos que requieren revisión manual del centro de mando.
     */
    public List<DocumentoRevisionResponse> obtenerDocumentosPendientes() {
        // En una implementación real, esto consultaría un microservicio de documentos o persistencia local del BFF.
        // Simulamos algunos datos basados en agendamientos existentes.
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos().join();
        
        return agendamientos.stream()
            .filter(a -> a.getIdContenedor() != null)
            .limit(5) // Solo mostramos los primeros 5 para revisión
            .map(a -> DocumentoRevisionResponse.builder()
                .idContenedor(a.getIdContenedor())
                .patenteCamion(a.getPatenteCamion())
                .tipoDocumento("BL / TATC")
                .estadoRevision("PENDIENTE")
                .fechaSubida(a.getHoraInicio().toString())
                .build())
            .collect(Collectors.toList());
    }
}
