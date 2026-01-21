package com.heim.api.drivers.application.service;

import com.heim.api.drivers.application.dto.DriverProfileDTO;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DriverProfileService {
    private final DriverRepository driverRepository;

    @Autowired
    DriverProfileService(DriverRepository driverRepository){
        this.driverRepository = driverRepository;
    }

    public DriverProfileDTO getProfileByUserId(Long userId){
        Driver driver = driverRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new DriverProfileDTO(
                driver.getUser().getFullName(),
                driver.getUser().getPhone(),
                driver.getUser().getUrlAvatarProfile()
        );
    }
}
