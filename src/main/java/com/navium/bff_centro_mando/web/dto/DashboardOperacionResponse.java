package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DashboardOperacionResponse {
    private Long idTurno;
    private String patenteCamion; // Patente del camión
    private LocalDateTime horaAgendada; // Hora agendada en formato ISO
    private String tipoOperacion; // Tipo de operación (ej. "CARGA", "DESCARGA")
    private String codigoContenedor; // Código del contenedor
    private String estadoContenedor; // Estado del contenedor (ej. "DISPONIBLE", "OCUPADO")
    private String andenAsignado;
}
