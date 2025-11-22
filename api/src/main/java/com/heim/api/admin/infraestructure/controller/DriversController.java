package com.heim.api.admin.infraestructure.controller;

import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.admin.application.service.impl.DriversServiceImpl;
import com.heim.api.drivers.infraestructure.controller.DriverController;
import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/admin/drivers/")
@CrossOrigin("*")
public class DriversController {
    private final DriversServiceImpl driversService;

    DriversController(DriversServiceImpl driversService){
        this.driversService = driversService;
    }
    @GetMapping("list/drivers")
    public ResponseEntity<Page<User>> listDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> pagedUsers = driversService.getUsers(pageable);
        return ResponseEntity.ok(pagedUsers);
    }

    @GetMapping("{driverId}/driver")
    public ResponseEntity<UserResponse> getDriverById(@PathVariable Long driverId) {
        try {
            UserResponse userResponse = driversService.findUserById(driverId);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{driverId}/update")
    public ResponseEntity<UserResponse> updatedDriverData(@PathVariable Long driverId, @RequestBody UserUpdateRequest userRequest) {
        try {
            if (userRequest == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            UserResponse updatedUser = driversService.updateUserData(driverId, userRequest);

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
