package com.heim.api.move.application.mapper;

import com.heim.api.move.application.dto.MoveDetailsDTO;
import com.heim.api.move.domain.entity.Move;

public class MoveSummaryDetailsMapper {
    public static MoveDetailsDTO toSummaryDTO(Move move) {
        MoveDetailsDTO dto = new MoveDetailsDTO();
        dto.setMoveId(move.getMoveId());
        dto.setOrigin(move.getOrigin());
        dto.setDestination(move.getDestination());
        dto.setDistance(move.getDistanceKm() + " km");
        dto.setPaymentMethod(move.getPaymentMethod());
        dto.setAmount(move.getPrice());

        return dto;
    }
}
