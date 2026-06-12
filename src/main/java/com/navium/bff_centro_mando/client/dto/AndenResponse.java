package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class AndenResponse {

    // Identificador del anden.
    private Long id;
    // Zona fisica o logica del anden.
    private String zona;
    // Numero del anden dentro de la zona.
    private int numero;
    // Codigo unico del anden.
    private String codigo;
    // Recibimos los enums como string para mantener el desacoplamiento entre microservicios.
    private String tipo; // Se pasa a string
    private String estado; // Se pasa a string
    private String sector; // Se pasa a string
    
}
