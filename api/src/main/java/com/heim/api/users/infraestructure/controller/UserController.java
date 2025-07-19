package com.heim.api.users.infraestructure.controller;

import com.heim.api.users.application.dto.UserRequest;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.application.service.UserService;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserService userService;

    @Autowired
    public  UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        System.out.println("Solicitud recibida: " + userRequest);
        try {
            UserResponse registerUser = userService.registerUser(userRequest);

           /* String token = jwtUtils.generateToken(registerUser.getEmail());
            UserRegistrationResponse response = new UserRegistrationResponse(registerUser, token); */

            return ResponseEntity.ok(registerUser);
        } catch (EmailAlreadyRegisteredException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Ocurrió un error inesperado. Nuestros desarrolladores ya fueron informados.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @GetMapping("user/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            // Obtener el usuario usando el servicio
            UserResponse userResponse = userService.getUserById(userId);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            // El usuario no se encuentra
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Error interno
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("update/{userId}")
    public ResponseEntity<UserResponse> updatedUserData(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        try {
            if (userRequest == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            UserResponse updatedUser = userService.updateUserData(userId, userRequest);

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            // Si el usuario no se encuentra
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            // En caso de que haya algún error con los datos (por ejemplo, email duplicado)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Error interno
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("delete/{userId}")
    public ResponseEntity<String> userDelete(@PathVariable Long userId) {
        try {
            userService.userDelete(userId);
            return ResponseEntity.ok("Usuario eliminado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Se produjo un error al procesar la solicitud");
        }
    }

}
