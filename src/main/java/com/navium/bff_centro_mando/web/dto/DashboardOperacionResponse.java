package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DashboardOperacionResponse {

    private Long idTurno;
    private String patenteCamion;
    private LocalDateTime horaAgendada;
    private String tipoOperacion;

    private String codigoContenedor;
    private String estadoContenedor;

    private String andenAsignado;
}
