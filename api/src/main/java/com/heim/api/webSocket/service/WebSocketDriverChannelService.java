package com.heim.api.webSocket.service;

import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketDriverChannelService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketDriverChannelService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyDriver(MoveNotificationDTO moveNotificationDTO, Long userIdToNotify) {
        String destination = "/topic/driver/available/" + userIdToNotify;

        System.out.println("Enviando notificación al canal: " + destination);
        messagingTemplate.convertAndSend(destination, moveNotificationDTO);
    }
}
