package com.heim.api.drivers.infraestructure.controller;

import com.heim.api.drivers.application.dto.DriverProfileDTO;
import com.heim.api.drivers.application.service.DriverProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@CrossOrigin("*")
public class DriverProfileController {
    private final DriverProfileService driverProfileService;

    @Autowired
    DriverProfileController(DriverProfileService driverProfileService){
        this.driverProfileService = driverProfileService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<DriverProfileDTO> getDriverProfile(@PathVariable Long userId) {
        DriverProfileDTO profile = driverProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
}
