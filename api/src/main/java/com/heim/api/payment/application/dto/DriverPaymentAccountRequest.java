package com.heim.api.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverPaymentAccountRequest {
    private Long driverId;
    private String paymentMethod;
    private String accountNumber;
}
