package com.navium.bff_centro_mando.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *   Interceptor para propagar el token JWT del BFF a los microservicios downstream.
 *  Este interceptor:
 *  - Extrae el token JWT del header Authorization de la request HTTP actual
 *  - Lo agrega al header Authorization de las requests que se hacen a los microservicios downstream
 *  - Permite que los microservicios validen el token del usuario original
 */

@Component
public class JwtPropagationInterceptor  implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, 
        byte[] body, 
        ClientHttpRequestExecution execution) throws IOException { 

        // Captura la peticion original que el usuario le hizo al BFF.
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) { 
            HttpServletRequest servletRequest = attributes.getRequest();

            // Extrae el token de la cabecera Authorization.
            String authorizationHeader = servletRequest.getHeader("Authorization");

            //Si existe el header, propagarlo a la request del microservicio
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { 
                request.getHeaders().add("Authorization", authorizationHeader);
            }
        }
        // Deja que la peticion continue hacia el microservicio.
        return execution.execute(request, body);
    }



}
