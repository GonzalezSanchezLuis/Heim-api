package com.heim.api.move.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptMoveRequest {
    private Long moveId;
    private Long driverId;
}
