package com.heim.api.drivers.domain.entity;


import com.heim.api.drivers.domain.enums.DriverStatus;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.users.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Driver {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String  licenseNumber;
    private String  vehicleType;
    private String  enrollVehicle;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DriverStatus status;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }



    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Move> trips = new ArrayList<>();

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + id +
                // Omitir la lista de 'reservationMovingList'
                '}';
    }

}
