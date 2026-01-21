package com.heim.api.webSocket.domain.entity.event;

import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import lombok.Data;


@Data
public class MoveAssignedEvent {
    private final MoveNotificationDTO notification;
    private final Long userId;
}
