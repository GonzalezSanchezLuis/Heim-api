package com.heim.api.users.infraestructure.controller;

import com.heim.api.auth.application.service.AuthService;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("auth")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String password = request.get("password");


        UserResponse userResponse = authService.authenticate(email, password);
        session.setAttribute("userId", userResponse.getUserId());
        session.setAttribute("role", userResponse.getRole());

        return ResponseEntity.ok(userResponse);
    }



    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout exitoso");
    }

}
