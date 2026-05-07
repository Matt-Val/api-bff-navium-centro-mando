package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class AndenResponse {

    private Long id;
    private String zona;
    private int numero;
    private String codigo;

    // Recibimos los enums como strings para mantener el desacoplamiento entre microservicios.
    private String tipo;
    private String estado;
    private String sector;
    
}
