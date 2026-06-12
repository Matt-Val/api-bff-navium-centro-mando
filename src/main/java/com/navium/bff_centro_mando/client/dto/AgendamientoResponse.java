package com.navium.bff_centro_mando.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamientoResponse {
    private Long id;
    @JsonProperty("idUsuario")
    private String usuarioId;
    private String correoUsuario;
    private String patenteCamion;
    private String rutChofer;
    @JsonProperty("contenedorId")
    private String idContenedor;
    private LocalDateTime horaInicio;
    private LocalDateTime bloqueFin;
    private String tipoOperacion;
    private String estadoAgendamiento;
}
