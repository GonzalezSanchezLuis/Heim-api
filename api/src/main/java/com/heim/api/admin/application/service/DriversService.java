package com.heim.api.admin.application.service;


import com.heim.api.admin.application.dto.DriverResponse;
import com.heim.api.admin.application.dto.DriverUpdateRequestDTO;
import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DriversService {
    Page<User> getDrivers(Pageable pageable);
    DriverResponse findDriverById(Long id);
    DriverResponse updateDriverData(Long id, DriverUpdateRequestDTO request);
}
