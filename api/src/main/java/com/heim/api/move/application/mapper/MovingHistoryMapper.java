package com.heim.api.move.application.mapper;


import com.heim.api.move.application.dto.MovingHistoryDTO;
import com.heim.api.move.domain.entity.Move;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface MovingHistoryMapper {
    @Mapping(source = "driver.enrollVehicle", target = "enrollVehicle")
    @Mapping(source = "driver.user.fullName", target = "name")
    @Mapping(source = "driver.user.urlAvatarProfile", target = "avatar")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "origin", target = "origin")
    @Mapping(source = "destination", target = "destination")
    MovingHistoryDTO toDto(Move move);

    List<MovingHistoryDTO> toDtoList(List<Move> moves);
}
