package com.heim.api.admin.infraestructure.controller;

import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.admin.application.service.impl.UsersServiceImpl;
import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.users.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/admin/users/")
@CrossOrigin("*")
public class UsersController {
    private final UsersServiceImpl usersServiceImpl;

    @Autowired
    public UsersController(UsersServiceImpl usersServiceImpl){
        this.usersServiceImpl = usersServiceImpl;

    }

    @GetMapping("list/users")
    public ResponseEntity<Page<User>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> pagedUsers = usersServiceImpl.getUsers(pageable);
        return ResponseEntity.ok(pagedUsers);
    }

    @GetMapping("{userId}/user")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = usersServiceImpl.findUserById(userId);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{userId}/update")
    public ResponseEntity<UserResponse> updatedUserData(@PathVariable Long userId, @RequestBody UserUpdateRequest userRequest) {
        try {
            if (userRequest == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            UserResponse updatedUser = usersServiceImpl.updateUserData(userId, userRequest);

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
}
