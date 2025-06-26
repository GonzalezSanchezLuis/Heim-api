package com.heim.api.move.application.mapper;

import com.heim.api.move.application.dto.MoveDTO;
import com.heim.api.move.domain.entity.Move;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MoveMapper {
    MoveDTO toDTO(Move move);
}
