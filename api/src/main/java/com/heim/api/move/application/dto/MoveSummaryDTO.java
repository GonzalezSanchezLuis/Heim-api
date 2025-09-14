package com.heim.api.move.application.dto;

import com.heim.api.move.domain.entity.Move;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MoveSummaryDTO {
    private Long moveId;
    private String origin;
    private String destination;
    private BigDecimal amount;
    private String paymentMethod;
    private String duration;
    private boolean paymentCompleted;

    public void MoveSummaryDto(Move move) {
        this.moveId = move.getMoveId();
        this.origin = move.getOrigin();
        this.destination = move.getDestination();
        this.amount = move.getPrice();
        this.paymentMethod = move.getPaymentMethod();
        this.duration = move.getDurationMin();
        this.paymentCompleted = false;

    }



}
