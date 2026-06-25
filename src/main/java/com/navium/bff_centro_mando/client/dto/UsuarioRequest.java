package com.navium.bff_centro_mando.client.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String rut;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private Boolean activo = true;
}