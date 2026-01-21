package com.heim.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler{

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }

    // 🚨 1. Manejo de Error de Conexión a la Base de Datos (Servidor Caído)
    @ExceptionHandler({DatabaseUnavailableException.class})
    public ResponseEntity<Map<String, String>> handleDatabaseUnavailable(DatabaseUnavailableException ex) {
        String userFriendlyMessage = "Hubo un error en los servidores, nuestros desarrolladores ya fueron informados.";
        return createErrorResponse(userFriendlyMessage, HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ExceptionHandler({IncorrectCredentialsException.class,
                        NotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleBadCredentials(IncorrectCredentialsException ex) {
        String message = "No dudamos que eres tú, pero no reconocemos tu contraseña";
        return createErrorResponse(message, HttpStatus.UNAUTHORIZED); // Retorna 401
    }
}
