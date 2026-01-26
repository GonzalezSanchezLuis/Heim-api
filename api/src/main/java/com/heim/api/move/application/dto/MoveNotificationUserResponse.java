package com.heim.api.move.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveNotificationUserResponse {
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private String enrollVehicle;
    private String vehicleType;
    private String driverImageUrl;
    private Long moveId;
    private String origin;
    private String destination;
    private Double driverLat;
    private Double driverLng;
}
