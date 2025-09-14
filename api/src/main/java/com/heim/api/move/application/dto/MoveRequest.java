package com.heim.api.move.application.dto;

import com.heim.api.price.domain.MoveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveRequest {
    private Long userId;
    private String origin;
    private String destination;
    private Double originLat;
    private Double originLng;
    private Double destinationLat;
    private Double destinationLng;
    private MoveType typeOfMove;
    private BigDecimal price;
    private String distanceKm;
    private String estimatedTime;
    private String paymentMethod;
}
