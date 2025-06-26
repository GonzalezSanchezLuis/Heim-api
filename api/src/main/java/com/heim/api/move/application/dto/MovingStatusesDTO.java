package com.heim.api.move.application.dto;

import com.heim.api.move.domain.enums.MoveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovingStatusesDTO {
    private Long moveId;
    private Long driverId;
    private LocalDateTime timestamp;

}
