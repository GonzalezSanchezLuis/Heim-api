package com.heim.api.admin.application.mapper;

import com.heim.api.admin.application.dto.DriverResponse;
import com.heim.api.admin.application.dto.DriverUpdateRequestDTO;
import com.heim.api.drivers.domain.entity.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriversMapper {
    DriverResponse toResponse(Driver driver);
    Driver toEntity(DriverUpdateRequestDTO driverUpdateRequest);
}
