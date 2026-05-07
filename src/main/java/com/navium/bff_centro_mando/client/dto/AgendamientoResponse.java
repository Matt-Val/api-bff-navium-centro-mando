package com.navium.bff_centro_mando.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamientoResponse {

    private Long id;
    private String usuarioId;
    private String patenteCamion;
    private String rutChofer;
    private String idContenedor;
    private LocalDateTime bloqueInicio;
    private LocalDateTime bloqueFin;


    // Nota: Los enums es mejor recibirlos como strings en bff.
    // Evitando tener que copiar los archivos de enums en el proyecto y mantenemos los microservicios desacoplados.
    
    private String tipoOperacion;
    private String estado;


}
