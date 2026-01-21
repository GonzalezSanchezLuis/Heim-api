package com.heim.api.drivers.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@Data
public class DriverResponse {
    private Long driverId;
    private String fullName;
    private String email;
    private String password;
    private String document;
    private String phone;
    private String licenseNumber;
    private String vehicleType;
    private String enrollVehicle;
    private String urlAvatarProfile;
    private String role;
    private LocalDateTime createdAt;
    private boolean active;
    private String status;



    public DriverResponse(
            Long driverId,
            String fullName,
            String email,
            String phone,
            String urlAvatarProfile,
            String document,
            String licenseNumber,
            String vehicleType,
            String enrollVehicle,
            String role,
            LocalDateTime createdAt,
            boolean active,
            Object o) {
    }
}
