package com.heim.api.payment.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentConfirmationRequest {
    private List<Long> driverIds;
    private String paymentBatchReference;
}
