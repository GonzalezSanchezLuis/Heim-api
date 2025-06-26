package com.heim.api.move.domain.entity;


import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.users.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "moves")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moveId;

    private String origin;
    private String destination;
    private Double originLat;
    private Double originLng;
    private Double destinationLat;
    private Double destinationLng;
    private String typeOfMove;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private String paymentMethod;

    private LocalDateTime requestTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private MoveStatus status;

    @ManyToOne
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
