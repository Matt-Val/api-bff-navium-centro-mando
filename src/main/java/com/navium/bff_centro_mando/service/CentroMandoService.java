package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import com.navium.bff_centro_mando.client.dto.ContenedorResponse;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CentroMandoService {
    private final AgendamientoClient agendamientoClient;
    private final ContenedoresClient contenedoresClient;

    public CentroMandoService(AgendamientoClient agendamientoClient, ContenedoresClient contenedoresClient) { 
        this.agendamientoClient = agendamientoClient;
        this.contenedoresClient = contenedoresClient;
    }

    public List<DashboardOperacionResponse> obtenerTableroPrincipal() {
        // 1. Llama al ms-agendamiento
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos();
        
        // 2. Llama a tu ms-contenedores
        List<ContenedorResponse> todosLosContenedores = contenedoresClient.obtenerTodosLosContenedores();

        // 3. Mezcla los datos para armar el DashboardOperacionResponse
        return agendamientos.stream().map(turno -> {
            
            // Busca el contenedor que coincida con el agendamiento
            String estadoContenedorReal = "DESCONOCIDO";
            if (turno.getIdContenedor() != null) {
                estadoContenedorReal = todosLosContenedores.stream()
                        .filter(c -> c.getCodigoSigla().equals(turno.getIdContenedor()))
                        .map(ContenedorResponse::getEstadoGeneral)
                        .findFirst()
                        .orElse("NO ENCONTRADO EN PATIO");
            }

            // Arma el JSON
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
