package com.heim.api.drivers.application.dto;

import lombok.Data;
@Data
public class DriverRequest {
    private Long userId;
    private String document;
    private String licenseCategory;
    private String  licenseNumber;
    private String  vehicleType;
    private String  enrollVehicle;
}
