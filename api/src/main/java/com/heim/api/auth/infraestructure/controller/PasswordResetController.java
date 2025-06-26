package com.heim.api.auth.infraestructure.controller;

import com.heim.api.auth.application.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin("*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(
            PasswordResetService passwordResetService
    ){
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody  Map<String,String> body){
        String email = body.get("email");
        passwordResetService.createPasswordResetToken(email);
        return ResponseEntity.ok("Correo de recuperacion enviado");

    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (passwordResetService.validateToken(token)) {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Contraseña actualizada");
        } else {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }
    }

}
