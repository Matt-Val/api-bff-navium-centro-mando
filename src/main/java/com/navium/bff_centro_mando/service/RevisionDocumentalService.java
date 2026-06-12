package com.navium.bff_centro_mando.service;

import com.navium.bff_centro_mando.client.AgendamientoClient;
import com.navium.bff_centro_mando.client.ContenedoresClient;
import com.navium.bff_centro_mando.client.dto.AgendamientoResponse;
import com.navium.bff_centro_mando.client.dto.ContenedorResponse;
import com.navium.bff_centro_mando.web.dto.DocumentoRevisionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RevisionDocumentalService {

    private final AgendamientoClient agendamientoClient;
    private final ContenedoresClient contenedoresClient;

    public RevisionDocumentalService(AgendamientoClient agendamientoClient, ContenedoresClient contenedoresClient) {
        this.agendamientoClient = agendamientoClient;
        this.contenedoresClient = contenedoresClient;
    }

    public void revisarDocumento(String idContenedor, String estado) {
        // El idContenedor que llega desde el front es el ID numérico
        Long id = Long.parseLong(idContenedor);

        // Actualizar el estado en el microservicio
        // Si el estado es APROBADO, liberamos BL y TATC
        String nuevoEstadoLegal = "APROBADO".equalsIgnoreCase(estado) ? "LIBERADO" : "RETENIDO";
        contenedoresClient.actualizarEstado(id, nuevoEstadoLegal, nuevoEstadoLegal).join();
    }

    public List<DocumentoRevisionResponse> obtenerDocumentosPendientes() {
        List<AgendamientoResponse> agendamientos = agendamientoClient.obtenerTodosLosAgendamientos().join();
        List<ContenedorResponse> contenedores = contenedoresClient.obtenerTodosLosContenedores().join();

        return agendamientos.stream()
            .filter(a -> a.getIdContenedor() != null)
            .map(a -> {
                // Encontrar el estado real del contenedor por su ID (a.getIdContenedor() es el ID numérico)
                ContenedorResponse c = contenedores.stream()
                    .filter(con -> con.getId() != null && con.getId().toString().equals(a.getIdContenedor()))
                    .findFirst()
                    .orElse(null);
                
                if (c == null) return null;

                // Solo mostramos como pendientes si están retenidos
                boolean estaPendiente = "RETENIDO".equalsIgnoreCase(c.getEstadoBL()) || "RETENIDO".equalsIgnoreCase(c.getEstadoTATC());
                if (!estaPendiente) return null;

                return DocumentoRevisionResponse.builder()
                    .idContenedor(c.getId().toString())
                    .siglaContenedor(c.getCodigoSigla())
                    .patenteCamion(a.getPatenteCamion())
                    .tipoDocumento("BL / TATC")
                    .estadoRevision("PENDIENTE")
                    .fechaSubida(a.getHoraInicio() != null ? a.getHoraInicio().toString() : "")
                    .build();
            })
            .filter(res -> res != null)
            .collect(Collectors.toList());
    }
}