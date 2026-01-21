package com.heim.api.fcm.application.service;

import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.fcm.application.dto.FcmTokenRequest;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.users.infraestructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public void registerToken(FcmTokenRequest request) {
        boolean ownerExists = switch (request.getOwnerType()) {
            case USER -> userRepository.existsById(request.getOwnerId());
            case DRIVER -> driverRepository.existsById(request.getOwnerId());
        };

        if (!ownerExists) {
            throw new IllegalArgumentException("El propietario no existe.");
        }

        tokenRepository.findByToken(request.getToken()).ifPresent(existing -> {
            throw new IllegalStateException("Este token ya está registrado.");
        });

        // Guardar el token
        FcmToken token = new FcmToken();
        token.setToken(request.getToken());
        token.setOwnerId(request.getOwnerId());
        token.setOwnerType(request.getOwnerType());
        System.out.println("DATOS DESDE EL CLIENTE" + request);

        tokenRepository.save(token);
        System.out.println("TOKEN GUARDADO");
    }
}
