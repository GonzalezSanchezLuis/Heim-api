package com.heim.api.drivers.application.mapper;

import com.heim.api.drivers.application.dto.DriverRequest;
import com.heim.api.drivers.application.dto.DriverResponse;
import com.heim.api.drivers.domain.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Mapping(target = "driverId", source = "id")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "document", source = "user.document")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "urlAvatarProfile", source = "user.urlAvatarProfile")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "active", source = "user.active")
    @Mapping(target = "createdAt", source = "createdAt")
    DriverResponse toResponse(Driver driver);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trips", ignore = true)
   // @Mapping(target = "active", ignore = true)
    Driver toEntity(DriverRequest driverRequest);
}
