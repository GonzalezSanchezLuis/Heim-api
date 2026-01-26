package com.heim.api.payment.domain;

import com.heim.api.payment.domain.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long driverId;
    private BigDecimal amount;
    private String reference;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;
}
