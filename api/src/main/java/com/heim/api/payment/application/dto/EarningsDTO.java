package com.heim.api.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningsDTO {
    private Long driverId;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
}
