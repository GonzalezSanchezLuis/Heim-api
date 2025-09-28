package com.heim.api.drivers.application.service;

import com.heim.api.drivers.application.dto.DriverProfileDTO;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DriverProfileService {
    private final UserRepository userRepository;

    @Autowired
    DriverProfileService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public DriverProfileDTO getProfileByUserId(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new DriverProfileDTO(
                user.getFullName(),
                user.getPhone(),
                user.getUrlAvatarProfile()
        );
    }
}
