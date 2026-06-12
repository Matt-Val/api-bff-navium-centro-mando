package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentoRevisionResponse {
    private String idContenedor; // ID numérico para acciones
    private String siglaContenedor; // Sigla alfanumérica para mostrar (ej. "CONT-1234")
    private String patenteCamion; // Patente del camión
    private String tipoDocumento; // BL, TATC, Guia
    private String estadoRevision; // PENDIENTE, APROBADO, RECHAZADO
    private String fechaSubida; // Fecha en formato ISO (ej. "2024-06-01T12:00:00Z")
}
