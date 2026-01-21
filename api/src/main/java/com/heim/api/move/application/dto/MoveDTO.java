package com.heim.api.move.application.dto;

import com.heim.api.move.domain.enums.MoveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveDTO {
    private Long moveId;
    private String origin;
    private String destination;
    private Double originLat;
    private Double originLng;
    private Double destinationLat;
    private Double destinationLng;
    private Double driverLat;
    private Double driverLng;
    private String typeOfMove;
    private BigDecimal price;
    private String paymentMethod;
    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MoveStatus status;
    private String distance;
    private String estimatedTimeOfArrival;
    private String distanceToDestination;
    private String timeToDestination;
    private String avatarProfile;
    private String fullName;
    private String enrollVehicle;
    private String vehicleType;
    private Long driverId;
    private Long userId;
}
