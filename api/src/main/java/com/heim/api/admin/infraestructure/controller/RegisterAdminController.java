package com.heim.api.admin.infraestructure.controller;

import com.heim.api.admin.application.dto.AdminRequest;
import com.heim.api.admin.application.dto.AdminResponse;
import com.heim.api.admin.application.service.AdminService;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/")
@CrossOrigin("*")
public class RegisterAdminController {
    private final AdminService adminService;

    @Autowired
    RegisterAdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRequest adminRequest){
        try {
            AdminResponse registerAdmin = adminService.registerAdmin(adminRequest);
            return ResponseEntity.ok(registerAdmin);

        } catch (EmailAlreadyRegisteredException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (Exception e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Ocurrió un error inesperado. Nuestros desarrolladores ya fueron informados.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> request, HttpSession session){
        String email = request.get("email");
        String password = request.get("password");

        AdminResponse adminResponse = adminService.authenticate(email,password);
        return ResponseEntity.ok(adminResponse);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout exitoso");
    }
}
