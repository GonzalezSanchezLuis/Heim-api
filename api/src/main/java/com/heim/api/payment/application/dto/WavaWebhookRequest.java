package com.heim.api.payment.application.dto;

import com.heim.api.payment.domain.enums.PayoutStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class WavaWebhookRequest {
    private String orderKey;
    @Enumerated(EnumType.STRING)
    private PayoutStatus paymentStatus;
    private Double amount;
    private String transactionId;
    private String message;
}
