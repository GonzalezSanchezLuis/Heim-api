package com.heim.api.payment.infraestructure.repository;


import com.heim.api.payment.domain.DriverBalance;
import org.aspectj.apache.bcel.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DriverBalanceRepository extends CrudRepository<DriverBalance, Long> {
    Optional<DriverBalance> findByDriverId(Long driverId);

}
