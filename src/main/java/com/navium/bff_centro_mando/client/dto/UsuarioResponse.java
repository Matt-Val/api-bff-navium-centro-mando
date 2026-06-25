package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String rut;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
}