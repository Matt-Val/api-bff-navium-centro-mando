package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
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

    // Inyecta los clientes necesarios para construir la vista del dashboard.
    public CentroMandoService(AgendamientoClient agendamientoClient, ContenedoresClient contenedoresClient) { 
        this.agendamientoClient = agendamientoClient;
        this.contenedoresClient = contenedoresClient;
    }

    // Construye la lista de operaciones combinando agendamientos y contenedores.
    public List<DashboardOperacionResponse> obtenerTableroPrincipal() {
        // 1. Llama al ms-agendamiento para obtener turnos.
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos();
        
        // 2. Llama al ms-contenedores para obtener el estado real.
        List<ContenedorResponse> todosLosContenedores = contenedoresClient.obtenerTodosLosContenedores();

        // 3. Mezcla los datos y arma la respuesta del dashboard.
        return agendamientos.stream().map(turno -> {
            
            // Busca el contenedor que coincida con el agendamiento.
            String estadoContenedorReal = "DESCONOCIDO";
            if (turno.getIdContenedor() != null) {
                estadoContenedorReal = todosLosContenedores.stream()
                        .filter(c -> c.getCodigoSigla().equals(turno.getIdContenedor()))
                        .map(ContenedorResponse::getEstadoGeneral)
                        .findFirst()
                        .orElse("NO ENCONTRADO EN PATIO");
            }

            // Arma el DTO que el controller devuelve al frontend.
            return DashboardOperacionResponse.builder()
                    .idTurno(turno.getId())
                    .patenteCamion(turno.getPatenteCamion())
                    .horaAgendada(turno.getBloqueInicio())
                    .tipoOperacion(turno.getTipoOperacion())
                    .codigoContenedor(turno.getIdContenedor())
                    .estadoContenedor(estadoContenedorReal)
                    .build();
                    
        }).collect(Collectors.toList());
    }
}
