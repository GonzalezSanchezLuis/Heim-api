package com.heim.api.drivers.infraestructure.controller;

import com.heim.api.drivers.application.dto.DriverProfileDTO;
import com.heim.api.drivers.application.service.DriverProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver")
@CrossOrigin("*")
public class DriverInformationController {
    private final DriverProfileService driverProfileService;

    @Autowired
    DriverInformationController(DriverProfileService driverProfileService){
        this.driverProfileService = driverProfileService;
    }

    @GetMapping("/{userId}/infromation")
    public ResponseEntity<DriverProfileDTO> getDriverProfile(@PathVariable Long userId) {
        DriverProfileDTO profile = driverProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }
}
