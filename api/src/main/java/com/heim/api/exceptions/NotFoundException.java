package com.heim.api.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }

    // Puedes agregar otros constructores si los necesitas
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
