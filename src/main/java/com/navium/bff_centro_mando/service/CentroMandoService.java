package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.AndenesClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import com.navium.bff_centro_mando.client.dto.AndenResponse;
import com.navium.bff_centro_mando.client.dto.ContenedorResponse;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
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

            // Busca el contenedor
            String estadoContenedorReal = "DESCONOCIDO";
            if (turno.getIdContenedor() != null) {
                estadoContenedorReal = todosLosContenedores.stream()
                    .filter(c -> c.getCodigoSigla().equals(turno.getIdContenedor()))
                    .map(ContenedorResponse::getEstadoGeneral)
                    .findFirst()
                    .orElse("NO ENCONTRADO EN PATIO");
            }

            // Buscar el Anden
            String nombreAnden = "SIN ANDEN ASIGNADO";
            if (turno.getCodigoAnden() != null) {
                nombreAnden = todosLosAndenes.stream()
                    .filter(a -> a.getCodigo().equals(turno.getCodigoAnden()))
                    .map(AndenResponse::getZona)
                    .findFirst()
                    .orElse("ANDEN NO REGISTRADO");
            }

            return DashboardOperacionResponse.builder()
                .idTurno(turno.getId())
                .patenteCamion(turno.getPatenteCamion())
                .horaAgendada(turno.getBloqueInicio())
                .tipoOperacion(turno.getTipoOperacion())
                .codigoContenedor(turno.getIdContenedor())
                .estadoContenedor(estadoContenedorReal)
                .andenAsignado(nombreAnden)
                .build();
        }).collect(Collectors.toList());
    }
}
