package com.heim.api.webSocket.infraestructure.listener;


import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.event.MoveAssignedEvent;
import com.heim.api.webSocket.service.MoveSocketNotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MoveSocketEventListener {
    private final MoveSocketNotificationService notificationService;

    public MoveSocketEventListener(MoveSocketNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleMoveAssignedEvent(MoveAssignedEvent event) {
        Move move = event.getMove();
        notificationService.notifyDriver(move);
    }
}
