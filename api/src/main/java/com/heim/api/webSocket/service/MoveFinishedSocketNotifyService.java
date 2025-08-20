package com.heim.api.webSocket.service;

import com.heim.api.payment.application.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MoveFinishedSocketNotifyService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MoveFinishedSocketNotifyService(SimpMessagingTemplate  messagingTemplate){
        this.messagingTemplate = messagingTemplate;

    }

    public void notifyMoveFinished(Long moveId, PaymentResponse paymentInfo) {
        String destination = "/topic/move/finished/" + moveId;
        System.out.println("Enviando notificación de fin de viaje al canal: " + destination);
        messagingTemplate.convertAndSend(destination, paymentInfo);
    }
}
