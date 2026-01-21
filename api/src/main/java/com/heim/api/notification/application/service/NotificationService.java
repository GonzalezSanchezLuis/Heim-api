package com.heim.api.notification.application.service;

import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.notification.infraestructure.firebase.FirebaseNotificationSender;
import com.heim.api.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseNotificationSender firebaseNotificationSender;
    private final FcmTokenRepository fcmTokenRepository;


    public  void notify(FcmToken.OwnerType ownerType, Long ownerId, String title, String body, Map<String, String> data, String message){
        List<String> targetTokens = getTargetTokens(ownerType,ownerId);
        firebaseNotificationSender.sendNotifications(targetTokens, title, body, data, message);
    }

    public void paymentNotification(FcmToken.OwnerType ownerType,
                                    Long ownerId,
                                    PaymentStatus status,
                                    BigDecimal amount) {

        String title =  status == PaymentStatus.APPROVED ? "Pago aprobado" : status == PaymentStatus.DECLINED ?
                "Pago rechazado" : "Pago pendiente";
        String description  = "Tu servicio de Mudanza";
        String body = description + " fue " + status.name().toLowerCase() + "por valor de " + amount + " COP" + " "+ ".";

        Map<String, String> data = Map.of(
                "paymentStatus", status.name(),
                "amount", amount.toString()
        );

        notify(ownerType,ownerId, title, body, data, null );
    }

    public void notifyUser(FcmToken.OwnerType ownerType, Long ownerId, String title, String body) {
        notify(ownerType, ownerId, title, body, null, null);
    }


    private List<String> getTargetTokens(FcmToken.OwnerType ownerType, Long ownerId) {
        return fcmTokenRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }
}
