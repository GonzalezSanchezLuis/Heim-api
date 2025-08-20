package com.heim.api.webSocket.service;

import com.heim.api.move.application.dto.MoveNotificationUserResponse;
import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MoveUserSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MoveUserSocketNotificationService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(Map<String, Object> moveNotificationUserResponse, Long userId){
        String destination = "/topic/user/" + userId;
        System.out.println("Enviando notificación al canal: " + destination);
        messagingTemplate.convertAndSend(destination, moveNotificationUserResponse);
    }
}
