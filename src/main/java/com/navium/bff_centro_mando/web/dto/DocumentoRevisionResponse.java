package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentoRevisionResponse {
    private String idContenedor;
    private String patenteCamion;
    private String tipoDocumento; // BL, TATC, Guia
    private String estadoRevision; // PENDIENTE, APROBADO, RECHAZADO
    private String fechaSubida;
}
