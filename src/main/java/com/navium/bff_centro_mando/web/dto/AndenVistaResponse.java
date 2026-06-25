package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AndenVistaResponse {
    private Long id;
    private String zona; // Zona a la que pertenece el anden
    private int numero; // Número del anden dentro de la zona
    private String codigo; // Código único del anden
    private String tipo; // Tipo de anden (ej. "CARGA", "DESCARGA")
    private String estado; // Estado del anden (ej. "DISPONIBLE", "OCUPADO")
    private String sector; // Sector al que pertenece el anden
    private String patenteOcupante; // Patente del camión que ocupa el anden
    private String contenedorOcupante; // Código del contenedor que ocupa el anden
}
