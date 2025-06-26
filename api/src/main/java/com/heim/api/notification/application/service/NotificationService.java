package com.heim.api.notification.application.service;

import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.notification.infraestructure.firebase.FirebaseNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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



    private List<String> getTargetTokens(FcmToken.OwnerType ownerType, Long ownerId) {
        return fcmTokenRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }
}
