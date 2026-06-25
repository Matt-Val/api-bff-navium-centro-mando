package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class ContenedorResponse {
    private Long id;
    private String codigoSigla;
    private String estadoGeneral;
    private String estadoBL;
    private String estadoTATC;
}
