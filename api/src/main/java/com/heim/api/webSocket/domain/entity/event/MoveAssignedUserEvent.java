package com.heim.api.webSocket.domain.entity.event;

import com.heim.api.move.application.dto.MoveNotificationUserResponse;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class MoveAssignedUserEvent {
    private final Map<String, Object> notificationPayload;
    private final Long driverId;

    public  MoveAssignedUserEvent(MoveNotificationUserResponse notification, Long driverId ){
      Map<String, Object> payload = new HashMap<>();
      payload.put("move", notification);
      this.notificationPayload = payload;
      this.driverId = driverId;
    }
}
