package com.heim.api.auth.infraestructure.controller;

import com.heim.api.auth.application.dto.ForgotPasswordRequest;
import com.heim.api.auth.application.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin("*")
public class PasswordResetController {

    private final EmailService passwordResetService;

    @Autowired
    public PasswordResetController(
            EmailService passwordResetService
    ){
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid  @RequestBody ForgotPasswordRequest forgotPasswordRequest){
        Map<String, String> response = new HashMap<>();
        try{
            passwordResetService.createPasswordResetToken(forgotPasswordRequest.getEmail());
            response.put("message", "Si el correo existe, recibirás un enlace de recuperación en breve.");
            return  ResponseEntity.ok(response);
        }catch (Exception e){
            System.err.println("Error en el proceso de recuperación: " + e.getMessage());

            response.put("error", "Hubo un problema al procesar la solicitud. Inténtalo más tarde.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }


    }

  /*  @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (passwordResetService.validateToken(token)) {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Contraseña actualizada");
        } else {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }
    }*/

}
