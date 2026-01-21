package com.heim.api.payment.application.service;

import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.move.application.service.MoveServiceWebhook;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.notification.application.service.NotificationService;
import com.heim.api.payment.application.dto.WavaWebhookRequest;

import org.springframework.stereotype.Service;

@Service
public class WavaWebhookService {
    private final MoveServiceWebhook moveServiceWebhook;
    private final NotificationService notificationService;

    public WavaWebhookService(MoveServiceWebhook moveServiceWebhook, NotificationService notificationService) {
        this.moveServiceWebhook = moveServiceWebhook;
        this.notificationService = notificationService;
    }

    public void processWebhook(WavaWebhookRequest request) {
        Long moveId = Long.parseLong(request.getOrderKey());
        Move move = moveServiceWebhook.findById(Long.parseLong(request.getOrderKey()));

        moveServiceWebhook.updatePaymentStatus(moveId, request.getPaymentStatus());

        notificationService.paymentNotification(
                FcmToken.OwnerType.USER,
                move.getUser().getUserId(),
                move.getPaymentStatus(),
                move.getPrice());

    }
}
