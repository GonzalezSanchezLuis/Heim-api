package com.heim.api.payment.infraestructure.repository;

import com.heim.api.payment.domain.Earning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EarningRepository extends JpaRepository<Earning, Long> {

    @Query("SELECT COALESCE(SUM(e.netAmount), 0.00) FROM Earning e WHERE e.driver.id = :driverId AND e.isSettled = false")
    BigDecimal sumPendingBalanceByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT MAX(e.settlementDate) FROM Earning e WHERE e.driver.id = :driverId AND e.isSettled = true")
    LocalDateTime findLastSettlementDateByDriverId(@Param("driverId") Long driverId);

    List<Earning> findAllByIsSettledFalse();
}
