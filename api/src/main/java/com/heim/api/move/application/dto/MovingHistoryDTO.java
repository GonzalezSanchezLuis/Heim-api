package com.heim.api.move.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovingHistoryDTO {
    private Long moveId;
    private String origin;
    private String destination;
    private String enrollVehicle;
    private String name;
    private String avatar;
    private String status;
}
