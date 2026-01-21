package com.heim.api.move.application.mapper;

import com.heim.api.move.application.dto.RestoreMoveResponseDTO;
import com.heim.api.move.domain.entity.Move;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ActiveMoveMapper {

    MoveMapper INSTANCE = Mappers.getMapper(MoveMapper.class);

    @Mapping(source = "driver.id", target = "driverId")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.urlAvatarProfile", target = "avatarProfile")
    @Mapping(source = "driver.enrollVehicle", target = "enrollVehicle")
    @Mapping(source = "driver.vehicleType", target = "vehicleType")
    @Mapping(source = "user.userId", target = "userId")
    RestoreMoveResponseDTO toDTO(Move move);
}
