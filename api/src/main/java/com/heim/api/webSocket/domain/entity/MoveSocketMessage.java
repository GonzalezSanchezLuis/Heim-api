package com.heim.api.webSocket.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveSocketMessage {
    private Long moveId;
    private String origin;
    private String destination;
    private LocalDateTime scheduledTime;

}
