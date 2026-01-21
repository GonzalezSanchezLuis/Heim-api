package com.heim.api.payment.infraestructure.repository;

import com.heim.api.payment.domain.DriverPaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverPaymentAccountRepository extends JpaRepository<DriverPaymentAccount, Long> {
    Optional<DriverPaymentAccount> findByDriverId(Long driverId);
}
