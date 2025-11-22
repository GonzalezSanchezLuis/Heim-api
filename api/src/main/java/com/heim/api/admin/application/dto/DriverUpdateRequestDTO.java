package com.heim.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequestDTO {
    private Long id;
    private String licenseNumber;
    private String vehicleType;
    private String enrollVehicle;

}
