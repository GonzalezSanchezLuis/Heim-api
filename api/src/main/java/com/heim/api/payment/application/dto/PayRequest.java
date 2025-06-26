package com.heim.api.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {
    private Double amount;
    private String paymentMethod; // "nequi", "daviplata", "tarjeta"
    private String name;
    private String email;
    private String phoneNumber;
}
