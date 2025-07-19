package com.heim.api.webSocket.service;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.webSocket.domain.entity.MoveSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MoveSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MoveSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyDriver(Move move) {
        System.out.println("Enviando notificación al canal: /topic/driver/notify/" + move.getDriver().getId());

        String destination = "/topic/driver/notify/" + move.getDriver().getId();
        messagingTemplate.convertAndSend(destination, move);
    }
}
