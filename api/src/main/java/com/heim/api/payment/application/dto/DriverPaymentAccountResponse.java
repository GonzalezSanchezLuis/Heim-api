package com.heim.api.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverPaymentAccountResponse {
    private Long driverId;
    private String paymentMethod;
    private String accountNumber;
}
