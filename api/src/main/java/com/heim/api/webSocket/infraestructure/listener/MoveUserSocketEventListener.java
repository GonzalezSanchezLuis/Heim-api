package com.heim.api.webSocket.infraestructure.listener;

import com.heim.api.webSocket.domain.entity.event.MoveAssignedUserEvent;
import com.heim.api.webSocket.service.WebSocketUserChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class MoveUserSocketEventListener {
    private final WebSocketUserChannelService moveUserSocketNotificationService;

    public MoveUserSocketEventListener(WebSocketUserChannelService moveUserSocketNotificationService) {
        this.moveUserSocketNotificationService = moveUserSocketNotificationService;
    }

    @EventListener
    public void handleMoveAssignedUserEvent(MoveAssignedUserEvent event){
        log.info("Evento MoveAssignedUserEvent disparado para el usuario ID: {}", event.getDriverId());
        moveUserSocketNotificationService.notifyUser(event.getNotificationPayload(), event.getDriverId());
    }
}
