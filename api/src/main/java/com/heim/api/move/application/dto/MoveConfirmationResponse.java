package com.heim.api.move.application.dto;

public class MoveConfirmationResponse {
    private String message;

    public MoveConfirmationResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}
