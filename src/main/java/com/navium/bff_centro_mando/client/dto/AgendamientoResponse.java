package com.navium.bff_centro_mando.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamientoResponse {
    // DTO que representa la respuesta del ms-agendamiento.
    // Si el microservicio cambia el formato, se ajusta aqui sin tocar la logica del BFF.

    private Long id;
    private String usuarioId;
    private String patenteCamion;
    private String rutChofer;
    private String idContenedor;
    private String codigoAnden;
    private LocalDateTime horaInicio;
    private LocalDateTime bloqueFin;

    // Nota: los enums se reciben como string para evitar acoplarse a enums de otros microservicios.
    private String tipoOperacion; // Se pasa a string
    private String estadoAgendamiento; // Se pasa a string
}
