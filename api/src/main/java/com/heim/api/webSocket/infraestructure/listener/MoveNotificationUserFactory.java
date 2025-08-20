package com.heim.api.webSocket.infraestructure.listener;

import com.heim.api.move.application.dto.MoveNotificationUserResponse;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.users.domain.entity.User;
import org.springframework.stereotype.Component;


@Component
public class MoveNotificationUserFactory {
    public MoveNotificationUserResponse build(Move move) {
        User driverUser = move.getDriver().getUser();

        return MoveNotificationUserResponse.builder()
                .driverName(driverUser.getFullName())
                .driverPhone(driverUser.getPhone())
                .driverImageUrl(driverUser.getUrlAvatarProfile())
                .enrollVehicle(move.getDriver().getEnrollVehicle())
                .vehicleType(move.getDriver().getVehicleType())
                .build();
    }
}

