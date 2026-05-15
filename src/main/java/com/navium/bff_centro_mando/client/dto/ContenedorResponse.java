package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class ContenedorResponse {

    // Codigo o sigla del contenedor (clave para cruzar con agendamientos).
    private String codigoSigla;
    // Estado general del contenedor segun el ms-contenedores.
    private String estadoGeneral;

    // Si en el futuro se necesita mas datos, se agregan aqui sin afectar a la logica.
}
