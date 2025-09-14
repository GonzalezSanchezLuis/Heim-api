package com.heim.api.move.application.dto;

import com.heim.api.price.domain.MoveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveDetailsDTO {
    private LocalDateTime movingDate;
    private Long moveId;
    private String origin;
    private String destination;
    private String distance;
    private BigDecimal amount;
    private boolean paymentCompleted;
    private String paymentMethod;
    private String driverName;
    private String typeOfVehicle;
    private MoveType typeOfMove;
   // private List<String> routePoints;
}
