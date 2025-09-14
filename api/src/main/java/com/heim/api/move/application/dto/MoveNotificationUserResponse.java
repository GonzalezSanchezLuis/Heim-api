package com.heim.api.move.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveNotificationUserResponse {
    private String driverName;
    private String driverPhone;
    private String enrollVehicle;
    private String vehicleType;
    private String driverImageUrl;
    private Long moveId;
}
