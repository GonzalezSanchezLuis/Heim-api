package com.heim.api.move.application.mapper;


import com.heim.api.move.application.dto.MoveSummaryDTO;
import com.heim.api.move.domain.entity.Move;

public class MoveSummaryMapper {
    public static MoveSummaryDTO toSummaryDTO(Move move) {
        MoveSummaryDTO dto = new MoveSummaryDTO();
        dto.setMoveId(move.getMoveId());
        dto.setOrigin(move.getOrigin());
        dto.setDestination(move.getDestination());
        dto.setAmount(move.getPrice());
        dto.setPaymentMethod(move.getPaymentMethod());
        dto.setDuration(move.getDurationMin());
        dto.setPaymentCompleted(false);
        return dto;
    }
}
