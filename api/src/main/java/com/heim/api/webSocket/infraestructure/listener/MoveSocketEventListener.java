package com.heim.api.webSocket.infraestructure.listener;

import com.heim.api.webSocket.domain.entity.event.MoveAssignedEvent;
import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import com.heim.api.webSocket.service.WebSocketDriverChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoveSocketEventListener {
    private final WebSocketDriverChannelService notificationService;

    public MoveSocketEventListener(WebSocketDriverChannelService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleMoveAssignedEvent(MoveAssignedEvent event) {
        MoveNotificationDTO notificationDTO = event.getNotification();
        log.info("Evento MoveAssignedEvent disparado para la mudanza ID: {}", notificationDTO.getMove().getMoveId());
        notificationService.notifyDriver(event.getNotification(), event.getUserId());
    }
}
