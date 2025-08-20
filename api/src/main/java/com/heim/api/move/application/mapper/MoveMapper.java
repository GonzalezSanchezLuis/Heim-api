package com.heim.api.move.application.mapper;

import com.heim.api.move.application.dto.MoveDTO;
import com.heim.api.move.domain.entity.Move;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoveMapper {
    @Mapping(target = "avatarProfile", source = "user.urlAvatarProfile", defaultValue = "")
    @Mapping(target = "enrollVehicle", source = "driver.enrollVehicle", defaultValue = "")
    @Mapping(target = "vehicleType", source = "driver.vehicleType", defaultValue = "")
    MoveDTO toDTO(Move move);
}
