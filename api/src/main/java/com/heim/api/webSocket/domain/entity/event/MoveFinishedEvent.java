package com.heim.api.webSocket.domain.entity.event;

import com.heim.api.payment.application.dto.PaymentResponse;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class MoveFinishedEvent {
    private final Long moveId;
    private final PaymentResponse paymentInfo;

    public MoveFinishedEvent(Long moveId, PaymentResponse paymentInfo) {
        this.moveId = moveId;
        this.paymentInfo = paymentInfo;
    }
}
