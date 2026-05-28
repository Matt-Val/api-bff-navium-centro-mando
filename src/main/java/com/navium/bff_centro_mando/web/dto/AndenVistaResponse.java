package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AndenVistaResponse {
    private Long id;
    private String zona;
    private int numero;
    private String codigo;
    private String tipo;
    private String estado;
    private String sector;
    
    // Información del ocupante actual si existe
    private String patenteOcupante;
    private String contenedorOcupante;
}
