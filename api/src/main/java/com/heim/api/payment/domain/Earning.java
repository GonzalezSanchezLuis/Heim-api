package com.heim.api.payment.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.move.domain.entity.Move;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data

public class Earning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long earningId;

    private boolean isSettled = false;
    private BigDecimal netAmount;
    private LocalDateTime settlementDate;
    private LocalDateTime CreationDate;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @JsonIgnore
    private Driver driver;

    @OneToOne
    @JoinColumn(name = "move_id")
    @JsonIgnore
    private Move move;
}
