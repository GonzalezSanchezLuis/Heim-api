package com.heim.api.price.application.dto;

import com.heim.api.price.domain.MoveType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceRequest {
    private MoveType typeOfMove;
    private String origin;
    private String destination;
    private BigDecimal originLat;
    private BigDecimal originLng;
    private BigDecimal destinationLat;
    private BigDecimal destinationLng;
    private BigDecimal price;
    private Long userId;
}
