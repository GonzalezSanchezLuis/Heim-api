package com.heim.api.payment.infraestructure.repository;

import com.heim.api.payment.domain.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long>{
    List<Payout> findByDriverId(Long driverId);
}
