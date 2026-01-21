package com.heim.api.move.application.service;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.payment.domain.PaymentStatus;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
public class MoveServiceWebhook {
    private final MoveRepository moveRepository;

    public MoveServiceWebhook(MoveRepository moveRepository){
        this.moveRepository = moveRepository;
    }

    public Move findById(Long moveId) {
        return moveRepository.findById(moveId)
                .orElseThrow(() -> new NoSuchElementException("Mudanza no encontrada con ID: " + moveId));
    }

    public void updatePaymentStatus(Long moveId, PaymentStatus paymentStatus) {
        Move move = findById(moveId);
        move.setPaymentStatus(paymentStatus);
        moveRepository.save(move);
    }
}
