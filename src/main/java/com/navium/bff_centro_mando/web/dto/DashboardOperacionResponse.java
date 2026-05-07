package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DashboardOperacionResponse {

    // Datos mezclados de varios ms.
    private Long idTurno;
    private String patenteCamion;
    private LocalDateTime horaAgendada;
    private String tipoOperacion;

    // Datos del contenedor
    private String codigoContenedor;
    private String estadoContenedor;
}
