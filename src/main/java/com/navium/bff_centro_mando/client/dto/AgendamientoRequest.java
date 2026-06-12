package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class AgendamientoRequest {
    private Long idUsuario;
    private String correoUsuario;
    private String patenteCamion;
    private String rutChofer;
    private String horaInicio;
    private String tipoOperacion;
    private String idContenedor;
}
