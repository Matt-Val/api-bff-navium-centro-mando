package com.navium.bff_centro_mando.web.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class EstadisticasDashboardResponse {
    private long totalAgendados;
    private long contenedoresEnPatio;
    private long contenedoresPerdidos;
    private Map<String, Long> distribucionPorEstado;
    private Map<String, Long> operacionesPorTipo;
}
