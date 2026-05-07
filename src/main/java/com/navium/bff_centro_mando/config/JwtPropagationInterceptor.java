package com.navium.bff_centro_mando.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

// Interceptor que copia el JWT de la solicitud entrante a las solicitudes salientes.
@Component
public class JwtPropagationInterceptor  implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException { 

        // Captura la peticion original que el usuario le hizo al BFF.
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) { 
            HttpServletRequest originalRequest = attributes.getRequest();
            // Extrae el token de la cabecera Authorization.
            String authHeader = originalRequest.getHeader(HttpHeaders.AUTHORIZATION);

            // Si hay un token valido, lo propaga a la solicitud saliente.
            if (authHeader != null && authHeader.startsWith("Bearer ")) { 
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
            }
        }

        // Deja que la peticion continue hacia el microservicio.
        return execution.execute(request, body);
    }



}
