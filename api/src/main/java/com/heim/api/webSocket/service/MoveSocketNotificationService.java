package com.heim.api.webSocket.service;

import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
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

    public void notifyDriver(MoveNotificationDTO moveNotificationDTO) {
        System.out.println("Enviando notificación al canal: /topic/driver/available" + moveNotificationDTO.getMove().getDriverId());

        String destination = "/topic/driver/available";
        messagingTemplate.convertAndSend(destination, moveNotificationDTO);
    }
}
