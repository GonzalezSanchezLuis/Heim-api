package com.heim.api.move.domain.event;

import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import lombok.Data;


@Data
public class MoveAssignedEvent {
    private final MoveNotificationDTO notification;
}
