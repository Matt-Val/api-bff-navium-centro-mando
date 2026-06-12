package com.navium.bff_centro_mando.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AndenOcupacionResponse {
    private String codigo;
    private String tipo;
    private String estado;
    private Long asignacionId;
    private String patenteTransporte;
    private Long contenedorId;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
}