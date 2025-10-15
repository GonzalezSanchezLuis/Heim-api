package com.heim.api.payment.infraestructure.repository;

import org.springframework.data.repository.Repository;
import com.heim.api.move.domain.entity.Move;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface EarningRepository extends Repository<Move, Long> {
    @Query("SELECT COALESCE(SUM(m.price), 0.00) FROM Move m WHERE m.driver.id = :driverId AND m.status = 'MOVE_COMPLETE' AND m.isPaid = false")
    BigDecimal sumUnpaidBalanceByDriverId(@Param("driverId") Long driverId);

    // Consulta para la Última Fecha de Pago (Last Payment Date)
    @Query("SELECT MAX(m.paymentDate) FROM Move m WHERE m.driver.id = :driverId AND m.isPaid = true")
    LocalDate findLastPaymentDateByDriverId(@Param("driverId") Long driverId);
}
