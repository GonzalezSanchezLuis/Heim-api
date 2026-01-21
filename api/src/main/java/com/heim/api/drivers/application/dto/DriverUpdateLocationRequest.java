package com.heim.api.drivers.application.dto;

import lombok.Data;

@Data
public class DriverUpdateLocationRequest {
    private Long driverId;
    private double latitude;
    private double longitude;
}
