package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.AndenesClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.UsuarioClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoRequest;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import com.navium.bff_centro_mando.client.dto.AndenOcupacionResponse;
import com.navium.bff_centro_mando.client.dto.AndenResponse;
import com.navium.bff_centro_mando.client.dto.ContenedorResponse;
import com.navium.bff_centro_mando.client.dto.UsuarioRequest;
import com.navium.bff_centro_mando.client.dto.UsuarioResponse;
import com.navium.bff_centro_mando.web.dto.DashboardOperacionResponse;
import com.navium.bff_centro_mando.web.dto.AndenVistaResponse;
import com.navium.bff_centro_mando.web.dto.EstadisticasDashboardResponse;
import org.springframework.stereotype.Service;

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
    // Cliente para gestionar usuarios.
    private final UsuarioClient usuarioClient;


    // Inyecta los clientes necesarios para construir la vista del dashboard.
    public CentroMandoService(
        AgendamientoClient agendamientoClient,
        ContenedoresClient contenedoresClient,
        AndenesClient andenesClient,
        UsuarioClient usuarioClient) {
            this.agendamientoClient = agendamientoClient;
            this.contenedoresClient = contenedoresClient;
            this.andenesClient = andenesClient;
            this.usuarioClient = usuarioClient;
    }


    // Construye la lista de operaciones combinando agendamientos y contenedores.
    public List<DashboardOperacionResponse> obtenerTableroPrincipal() {
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos().join();
        List<ContenedorResponse> todosLosContenedores = contenedoresClient.obtenerTodosLosContenedores().join();

        return agendamientos.stream().map(turno -> {

            // 1. Lógica del Contenedor
            String estadoContenedorReal = "NO ENCONTRADO EN PATIO";
            String codigoContenedorReal = "--";

            if (turno.getIdContenedor() != null) {
                ContenedorResponse contenedorEncontrado = todosLosContenedores.stream()
                    .filter(c -> c.getId() != null && c.getId().toString().equals(turno.getIdContenedor()))
                    .findFirst()
                    .orElse(null);

                if (contenedorEncontrado != null) {
                    estadoContenedorReal = contenedorEncontrado.getEstadoGeneral();
                    codigoContenedorReal = contenedorEncontrado.getCodigoSigla();
                }
            }

            return DashboardOperacionResponse.builder()
                .idTurno(turno.getId())
                .patenteCamion(turno.getPatenteCamion())
                .horaAgendada(turno.getHoraInicio())
                .tipoOperacion(turno.getTipoOperacion())
                .codigoContenedor(codigoContenedorReal)
                .estadoContenedor(estadoContenedorReal)
                .andenAsignado("SIN ANDEN ASIGNADO")
                .build();
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene la vista espacial de los andenes con la información de quién los ocupa.
     */
    public List<AndenVistaResponse> obtenerMapaAndenes() {
        List<AndenResponse> andenes = andenesClient.obtenerTodos().join();
        List<AndenOcupacionResponse> ocupaciones = andenesClient.obtenerOcupacionesActivas().join();
        List<ContenedorResponse> todosLosContenedores = contenedoresClient.obtenerTodosLosContenedores().join();

        return andenes.stream().map(anden -> {
            AndenOcupacionResponse ocupacion = ocupaciones.stream()
                .filter(o -> o.getCodigo() != null && o.getCodigo().equalsIgnoreCase(anden.getCodigo()))
                .findFirst()
                .orElse(null);

            String codigoContenedor = null;
            if (ocupacion != null && ocupacion.getContenedorId() != null) {
                codigoContenedor = todosLosContenedores.stream()
                    .filter(c -> c.getId() != null && c.getId().equals(ocupacion.getContenedorId()))
                    .map(ContenedorResponse::getCodigoSigla)
                    .findFirst()
                    .orElse(ocupacion.getContenedorId().toString());
            }

            return AndenVistaResponse.builder()
                .id(anden.getId())
                .zona(anden.getZona())
                .numero(anden.getNumero())
                .codigo(anden.getCodigo())
                .tipo(anden.getTipo())
                .estado(anden.getEstado())
                .sector(anden.getSector())
                .patenteOcupante(ocupacion != null ? ocupacion.getPatenteTransporte() : null)
                .contenedorOcupante(codigoContenedor)
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

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioClient.listarTodos().join();
    }

    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        return usuarioClient.crearUsuario(request).join();
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        return usuarioClient.actualizarUsuario(id, request).join();
    }

    public void desactivarUsuario(Long id) {
        usuarioClient.desactivarUsuario(id).join();
    }

    public void activarUsuario(Long id) {
        usuarioClient.activarUsuario(id).join();
    }
}

